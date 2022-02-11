package ir.amozkade.advancedAsisstiveTouche.helper.bindings;

import android.widget.ImageView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.BindingAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import ir.amozkade.advancedAsisstiveTouche.R;
import ir.mobitrain.applicationcore.api.BaseAPI;
import okhttp3.OkHttpClient;

public class ImageViewBinding {

    @BindingAdapter("downloadImage")
    public static void downloadImage(AppCompatImageView imageView, int productId) {
        String url = BaseAPI.getInstance().getBase().replace("https", "http") + "Image/TopImageForProduct/" + productId;
        Picasso.get().load(url).placeholder(R.drawable.ic_placeholder).into(imageView);
    }

    @BindingAdapter("downloadImageForRoundImageView")
    public static void setRoundImage(RoundedImageView imageView, int productId) {
        String url = BaseAPI.getInstance().getBase().replace("https", "http") + "Image/TopImageForProduct/" + productId;
        Picasso.get().load(url).placeholder(R.drawable.ic_placeholder).into(imageView);
    }

    @BindingAdapter("downloadImageWithName")
    public static void setImageWithName(ImageView imageView, String imageName) {
        String url = BaseAPI.getInstance().getBase().replace("https", "http").replace("Api", "") + "/Images/" + imageName;
        Picasso.get().load(url).placeholder(R.drawable.ic_circle_palceholder).into(imageView);
    }


    @BindingAdapter("downloadImageWithUrl")
    public static void setImageWithUrl(ImageView imageView, String url) {
        Picasso.get().load(url).placeholder(R.drawable.ic_placeholder).into(imageView);
    }

    @BindingAdapter({"fileName", "qu"})
    public static void setImageWithNameAndQuality(AppCompatImageView imageView, String fileName, String qu) {
        String url = BaseAPI.getInstance().getBase().replace("https", "http") + "Image/ImageWithCustomQuality/" + fileName + "/" + qu;
        Picasso.get().load(url).placeholder(R.drawable.ic_placeholder).into(imageView);
    }

    public static void downloadProfileImage(@NotNull CircleImageView imageView, @NotNull String url) {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
        Picasso picasso = new Picasso.Builder(imageView.getContext()).downloader(new OkHttp3Downloader(client)).build();
        picasso.load(url).placeholder(R.drawable.img_avatar).into(imageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
}
