package com.yourname.cinemate.data.remote;

// import các model bạn sẽ tạo ở bước sau
import com.yourname.cinemate.data.model.Attachment;
import com.yourname.cinemate.data.model.ChangePasswordDto;
import com.yourname.cinemate.data.model.Comment;
import com.yourname.cinemate.data.model.ConversationResponse;
import com.yourname.cinemate.data.model.CreateCommentDto;
import com.yourname.cinemate.data.model.CreateRatingDto;
import com.yourname.cinemate.data.model.ForgotPasswordDto;
import com.yourname.cinemate.data.model.GoogleTokenDto;
import com.yourname.cinemate.data.model.LoginDto;
import com.yourname.cinemate.data.model.LoginResponse;
import com.yourname.cinemate.data.model.Movie;
import com.yourname.cinemate.data.model.PaginatedComments;
import com.yourname.cinemate.data.model.PaginatedMovies;
import com.yourname.cinemate.data.model.Rating;
import com.yourname.cinemate.data.model.RefreshTokenDto;
import com.yourname.cinemate.data.model.RegisterDto;
import com.yourname.cinemate.data.model.RegisterResponse;
import com.yourname.cinemate.data.model.ResetPasswordDto;
import com.yourname.cinemate.data.model.ShareLinks;
import com.yourname.cinemate.data.model.UpdateUserDto;
import com.yourname.cinemate.data.model.User;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {


    // --- AUTHENTICATION MODULE ---

    @POST("auth/register")
    Call<RegisterResponse> registerUser(@Body RegisterDto registerDto);

    @POST("auth/login")
    Call<LoginResponse> loginUser(@Body LoginDto loginDto);

    @GET("auth/profile")
    Call<User> getProfile();

    // --- MOVIES MODULE ---

    @GET("movies")
    Call<PaginatedMovies> getAllMovies(
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("search") String search
    );

    @GET("movies/{id}")
    Call<Movie> getMovieById(@Path("id") int movieId);

    // --- WATCHLIST MODULE ---

    @GET("watchlist")
    Call<List<Movie>> getWatchlist();

    @POST("watchlist/{movieId}")
    Call<Movie> addToWatchlist(@Path("movieId") int movieId);

    @DELETE("watchlist/{movieId}")
    Call<Void> removeFromWatchlist(@Path("movieId") int movieId); // Thường DELETE không trả về body

    // --- RATINGS MODULE ---

    @POST("ratings/{movieId}")
    Call<Rating> rateMovie(@Path("movieId") int movieId ,@Body CreateRatingDto ratingDto);

    // --- COMMENTS MODULE ---

    @GET("movies/{movieId}/comments")
    Call<PaginatedComments> getMovieComments(@Path("movieId") int movieId, @Query("page") int page, @Query("limit") int limit);

    // Tạo một bình luận mới
    @POST("movies/{movieId}/comments")
    // <-- ĐƯỜNG DẪN MỚI
    Call<Comment> createComment(
            @Path("movieId") int movieId, // <-- movieId được truyền trong URL
            @Body CreateCommentDto contentDto // Body bây giờ chỉ cần chứa content
    );

    @GET("movies/{movieId}/comments/{commentId}/replies")
    Call<PaginatedComments> getCommentReplies(
            @Path("movieId") int movieId,
            @Path("commentId") String commentId,
            @Query("page") int page,
            @Query("limit") int limit
    );
    // --- RECOMMENDATIONS MODULE ---

    @GET("recommendations/for-you")
    Call<List<Movie>> getMoviesForYou();
    @GET("movies/popular")
    Call<List<Movie>> getPopularMovies();
    @GET("movies/random")
    Call<List<Movie>> getRandomMovies();
    @GET("movies/top-rated")
    // Hoặc đường dẫn tương ứng mà BE cung cấp
    Call<List<Movie>> getTopRatedMovies();
    @GET("movies/search")
    Call<PaginatedMovies> searchMovies(@Query("query") String query);
    @PUT("profile")
    Call<User> updateUserProfile(@Body UpdateUserDto updateUserDto);
    @Multipart
    @PUT("profile/avatar")
    Call<User> updateUserAvatar(@Part MultipartBody.Part avatarFile);
    // Đổi mật khẩu người dùng
    @PUT("profile/change-password") // Hãy xác nhận lại endpoint này với backend
    Call<Void> changePassword(@Body ChangePasswordDto changePasswordDto); // Thường không cần body trả về
    // Endpoint để ghi nhận lịch sử xem
    @POST("tracking/view/{movieId}")
    Call<Void> trackView(@Path("movieId") int movieId);
    @POST("auth/refresh")
    Call<LoginResponse> refreshToken(@Body RefreshTokenDto refreshTokenDto);
    @POST("auth/google/token")
    Call<LoginResponse> googleSignIn(@Body GoogleTokenDto googleTokenDto);
    // Trong ApiService.java
    @POST("auth/forgot-password")
    Call<Void> requestPasswordReset(@Body ForgotPasswordDto emailDto);

    @POST("auth/reset-password")
    Call<Void> resetPassword(@Body ResetPasswordDto resetDto);
    // Lấy cuộc hội thoại và lịch sử tin nhắn
    @GET("chat/conversation")
    Call<ConversationResponse> getUserConversation();

    // Upload file (ảnh)
    @Multipart
    @POST("chat/upload")
    Call<Attachment> uploadChatFile(@Part MultipartBody.Part file);
    @GET("movies/{id}/share")
    Call<ShareLinks> getShareLinks(@Path("id") int movieId);
}
