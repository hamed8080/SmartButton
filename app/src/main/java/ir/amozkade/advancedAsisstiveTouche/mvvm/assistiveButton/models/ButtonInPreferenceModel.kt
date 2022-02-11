package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.models

import android.content.Context
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import ir.amozkade.advancedAsisstiveTouche.BaseApp
import ir.amozkade.advancedAsisstiveTouche.mvvm.editButtonPositions.EditPositionRepository
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonIgnoreProperties(ignoreUnknown = true)
class ButtonInPreferenceModel @JsonCreator constructor(@JsonProperty("title") val titleId: String? = null,
                                                       @JsonProperty("contactNam") val contactName: String? = null,
                                                       @JsonProperty("unicodeIcon") val unicodeIcon: String? = null,
                                                       @JsonProperty("needRoot") val needRoot: Boolean = false,
                                                       @JsonProperty("buttonTypeName") val buttonTypeName: String? = null,
                                                       @JsonProperty("appName") val appName: String? = null,
                                                       @JsonProperty("imageUri") val imageUri: String? = null,
                                                       @JsonProperty("packageName") val packageName: String? = null,
                                                       @JsonProperty("phoneNumber") val phoneNumber: String? = null,
                                                       @JsonProperty("requireApi") val requiredApi: Int? = null,
                                                       @JsonProperty("orderPositionInPanel") val orderPositionInPanel: Int? = null
) : Parcelable {
    companion object {
        fun convertButtonModelInPanelToPreference(button: ButtonModelInPanel): ButtonInPreferenceModel {
            return ButtonInPreferenceModel(
                    titleId = button.titleId,
                    contactName = button.contactName,
                    unicodeIcon = button.unicodeIcon,
                    imageUri = button.imageUri,
                    packageName = button.packageName,
                    needRoot = button.needRoot,
                    phoneNumber = button.phoneNumber,
                    appName = button.appName,
                    buttonTypeName = button.buttonTypeName?.name,
                    orderPositionInPanel = button.orderPositionInPanel,
                    requiredApi = button.requiredApi
            )
        }


        fun convertPreferenceToButtonInPanel(prefButton: ButtonInPreferenceModel,  context: Context): ButtonModelInPanel {
            return ButtonModelInPanel(titleId = prefButton.titleId,
                    contactName = prefButton.contactName,
                    needRoot = prefButton.needRoot,
                    buttonTypeName = ButtonModelInPanel.ButtonTypesName.valueOf(prefButton.buttonTypeName!!),
                    button = ButtonFactory.buttonFactory.create(ButtonModelInPanel.ButtonTypesName.valueOf(prefButton.buttonTypeName)),
                    unicodeIcon = prefButton.unicodeIcon,
                    icon = EditPositionRepository.convertImageUriStringToDrawable(prefButton.packageName, prefButton.imageUri, context),
                    appName = prefButton.appName,
                    packageName = prefButton.packageName,
                    phoneNumber = prefButton.phoneNumber,
                    imageUri = prefButton.imageUri,
                    requiredApi = prefButton.requiredApi
            )
        }
    }

}
