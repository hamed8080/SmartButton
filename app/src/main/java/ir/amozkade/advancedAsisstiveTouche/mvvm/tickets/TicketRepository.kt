package ir.amozkade.advancedAsisstiveTouche.mvvm.tickets

import android.os.Build
import com.fasterxml.jackson.databind.ObjectMapper
import ir.amozkade.advancedAsisstiveTouche.BuildConfig
import ir.amozkade.advancedAsisstiveTouche.helper.api.DataState
import ir.amozkade.advancedAsisstiveTouche.mvvm.preferenceActivity.SettingRepository
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.conversation.model.TicketMessage
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.conversation.utils.TicketConversationResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.di.TicketDao
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.di.TicketRetrofit
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.model.Ticket
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.model.TicketStatus
import ir.amozkade.advancedAsisstiveTouche.mvvm.tickets.utils.TicketResponse
import ir.amozkade.advancedAsisstiveTouche.mvvm.user.login.models.Payload
import ir.mobitrain.applicationcore.api.JWT
import ir.mobitrain.applicationcore.logger.LoggerDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TicketRepository @Inject constructor(
        private val ticketRetrofit: TicketRetrofit,
        private val loggerDao: LoggerDao,
        private val ticketDao: TicketDao,
        private val settingRepository: SettingRepository
) {

    suspend fun addNewMessage(ticket: TicketMessage): Flow<DataState<TicketConversationResponse>> = flow {
        emit(DataState.Loading)
        val ticketMessageResult = ticketRetrofit.addUserTicketMessage(ticket)
        ticketDao.insertTicketMessage(ticketMessageResult)
        emit(DataState.Success(TicketConversationResponse.AddedTicket(ticketMessageResult)))
    }

    suspend fun getAllTicketMessages(ticketId: Int): Flow<DataState<TicketConversationResponse>> = flow {
        emit(DataState.Loading)

        val dbMessages = ticketDao.getAllTicketMessages(ticketId)
        emit(DataState.Success(TicketConversationResponse.TicketMessages(dbMessages)))

        val tickets = ticketRetrofit.getAllTicketMessages(ticketId)
        ticketDao.insertAllTicketMessages(tickets)
        emit(DataState.Success(TicketConversationResponse.TicketMessages(tickets)))
    }

    suspend fun startNewTicket(title: String): Flow<DataState<TicketResponse>> = flow {
        emit(DataState.Loading)
        val ticket = getStartTicket(title)
        ticket?.let {
            val resultTicket = ticketRetrofit.startNewTicket(it)
            loggerDao.deleteAll()
            ticketDao.insertTicket(resultTicket)
            emit(DataState.Success(TicketResponse.TicketAdded(resultTicket)))
        }
    }

    suspend fun getAllTickets(): Flow<DataState<TicketResponse>> = flow {
        emit(DataState.Loading)
        val dbTickets = ticketDao.getAllTickets()
        emit(DataState.Success(TicketResponse.AllTickets(dbTickets)))
        JWT.instance.getPayload<Payload>()?.userId?.let { userId ->
            val tickets = ticketRetrofit.getAllTickets(userId)
            ticketDao.insertAllTickets(tickets)
            emit(DataState.Success(TicketResponse.AllTickets(tickets)))
        }
    }

    private fun getStartTicket(title: String): Ticket? {
        val userId = JWT.instance.getPayload<Payload>()?.userId ?: return null
        val deviceName = "${Build.MANUFACTURER}-${Build.MODEL}"
        val sdkApi = Build.VERSION.SDK_INT
        val appVersion = BuildConfig.VERSION_NAME
        val logData = getLogs()
        return Ticket(0, title, userId, settingRepository.getCashedModel().firebaseToken, sdkApi, deviceName, logData, appVersion, null, TicketStatus.IN_PROGRESS)
    }

    private fun getLogs(): String {
        var logData = ""
        val logs = loggerDao.getAllLogs()
        logs.forEach {
            logData += "*********************\n"
            logData += ObjectMapper().writeValueAsString(it)
            logData += "*********************\\n"
        }
        return logData
    }
}