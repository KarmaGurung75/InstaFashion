package com.hdd.instaFashion

import com.hdd.instaFashion.data.models.*
import com.hdd.instaFashion.data.remoteDataSource.ServiceBuilder
import com.hdd.instaFashion.repository.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ValidationUnitTest {
    private lateinit var userRepository: UserRepository
    private lateinit var postRepository: PostRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var reviewRatingRepository: ReviewRatingRepository
    private lateinit var likeRepository: LikeRepository
    private lateinit var commentRepository: CommentRepository
<<<<<<< HEAD
    private lateinit var shopRepository: ShopRepository
=======
    private lateinit var notificationRepository: NotificationRepository
>>>>>>> notification_branch

    private val HIRA :User = User(email="user@pakwan.com", password = "12345")
    private val ADITI :User = User(email="aditipoudel59@gmail.com", password = "qwerty")
    private val RACHEL :User = User(email="rachel@pakwan.com", password = "12345")
    private val JAYAS :User = User(email="pzayazz@gmail.com", password = "123456")
    private val ARBIN :User = User(email="arbin@pakwan.com", password = "arbin123")
    private val TEST_USER :User = User(email="hello@gmail.com", password = "1234567")

    @Test
    fun loginUser() = runBlocking {
        userRepository = UserRepository()
        val response = userRepository.loginUser(RACHEL)
        val expectedResult = true
        val actualResult = response.success
        assertEquals(expectedResult, actualResult)
    }
    @Test
    fun registerUser() = runBlocking {
        val user = User(
            fullname = "Arbin Dhakal",
            email = "arbin@pakwan.com",
            password = "arbin123",
            confirmPassword = "arbin123",
        )
        userRepository = UserRepository()
        val response = userRepository.registerUser(user)
        val expectedResult = true
        val actualResult = response.success
        assertEquals(expectedResult, actualResult)
    }
    @Test
    fun getUserProfile() = runBlocking {
        userRepository = UserRepository()
        val accessToken = "Bearer " + userRepository.loginUser(JAYAS).accessToken
        ServiceBuilder.token=accessToken
        val response = userRepository.getUserProfile()
        val expectedResult = true
        val actualResult = response.success
        assertEquals(expectedResult, actualResult)
    }
    @Test
    fun addPost() = runBlocking {
        userRepository = UserRepository()
        postRepository = PostRepository()
        val accessToken = "Bearer " + userRepository.loginUser(RACHEL).accessToken
        ServiceBuilder.token=accessToken
        val response = postRepository.addPostWithoutImage(Post(status = "This is test Post Again"))
        val expectedResult = true
        val actualResult = response.success
        assertEquals(expectedResult, actualResult)
    }
    @Test
    fun getRecipeById() = runBlocking {
        userRepository = UserRepository()
        productRepository = ProductRepository()
        val accessToken = "Bearer " + userRepository.loginUser(HIRA).accessToken
        ServiceBuilder.token=accessToken
        val response = productRepository.getRecipeById("61f16582333566d837eac982")
        val expectedResult = true
        val actualResult = response.success
        assertEquals(expectedResult, actualResult)
    }
    @Test
    fun addReviewRating() = runBlocking {
        reviewRatingRepository = ReviewRatingRepository()
        userRepository = UserRepository()
        val accessToken = "Bearer " + userRepository.loginUser(HIRA).accessToken
        ServiceBuilder.token=accessToken
        val response = reviewRatingRepository.addReviewRating("61cabcecb95707d20244cc22",
            ReviewRating(review = "This is unit test", rating = 4)
        );
        val expectedResult = true
        val actualResult = response.success!!
        assertEquals(expectedResult, actualResult)
    }
    @Test
    fun updateLike() = runBlocking {
        likeRepository = LikeRepository()
        userRepository = UserRepository()
        val accessToken = "Bearer " + userRepository.loginUser(HIRA).accessToken
        ServiceBuilder.token=accessToken
        val response = likeRepository.updateLike("61cabcecb95707d20244cc22");
        val expectedResult = false
        val actualResult = response.success
        assertEquals(expectedResult, actualResult)
    }
    @Test
    fun addComment() = runBlocking {
        commentRepository = CommentRepository()
        userRepository = UserRepository()
        val accessToken = "Bearer " + userRepository.loginUser(HIRA).accessToken
        ServiceBuilder.token=accessToken
        val response = commentRepository.addComment("61cabcecb95707d20244cc22",
            Comment(comment = "This is unit test")
        );
        val expectedResult = true
        val actualResult = response.success!!
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun changePassword() = runBlocking {
        userRepository = UserRepository()
        val accessToken = "Bearer " + userRepository.loginUser(TEST_USER).accessToken
        ServiceBuilder.token=accessToken
        val response = userRepository.changePassword(User(oldPassword = "1234567", newPassword = "123456"))
        val expectedResult = true
        val actualResult = response.success!!
        assertEquals(expectedResult, actualResult)
    }


    @Test
    fun viewOtherProfile() = runBlocking {
        userRepository = UserRepository()
        val accessToken = "Bearer " + userRepository.loginUser(TEST_USER).accessToken
        ServiceBuilder.token=accessToken
        val response = userRepository.getOtherUserProfile("61d45eb1fa850aea91ed8d6e");
        val expectedResult = false
        val actualResult = response.success
        assertEquals(expectedResult, actualResult)
    }
    @Test
    fun getFollowingPost() = runBlocking {
        userRepository = UserRepository()
        postRepository = PostRepository()
        val accessToken = "Bearer " + userRepository.loginUser(ADITI).accessToken
        ServiceBuilder.token=accessToken
        val response = postRepository.getFollowingPost();
        val expectedResult = true
        val actualResult = response.success
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun getTrendingPost() = runBlocking {
        userRepository = UserRepository()
        postRepository = PostRepository()
        val accessToken = "Bearer " + userRepository.loginUser(ADITI).accessToken
        ServiceBuilder.token=accessToken
        val response = postRepository.getTrendingPost();
        val expectedResult = true
        val actualResult = response.success
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun followUser() = runBlocking {
        userRepository = UserRepository()
        val accessToken = "Bearer " + userRepository.loginUser(JAYAS).accessToken
        ServiceBuilder.token=accessToken
        val response = userRepository.followUser("61d45eb1fa850aea91ed8d6e");
        val expectedResult = true
        val actualResult = response.success
        assertEquals(expectedResult, actualResult)
    }
    @Test
    fun savedRecipe() = runBlocking {
        userRepository = UserRepository()
        val accessToken = "Bearer " + userRepository.loginUser(ADITI).accessToken
        ServiceBuilder.token=accessToken
        val response = userRepository.savedRecipe("61dbb376b3fcbebfd133feab")
        val expectedResult = true
        val actualResult = response.success
        assertEquals(expectedResult, actualResult)
    }
    @Test
    fun editComment() = runBlocking {
        commentRepository = CommentRepository()
        userRepository = UserRepository()
        val accessToken = "Bearer " + userRepository.loginUser(ARBIN).accessToken
        ServiceBuilder.token=accessToken
        val response = commentRepository.editComment("61d45c5bd60e59afdc3523ae",
            Comment(comment = "Comment edited")
        );
        val expectedResult = true
        val actualResult = response.success!!
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun shareRecipe() = runBlocking {
        userRepository = UserRepository()
        productRepository = ProductRepository()
        val accessToken = "Bearer " + userRepository.loginUser(JAYAS).accessToken
        ServiceBuilder.token=accessToken
        val response = productRepository.shareRecipe("61de84132470bc2880e00c75")
        val expectedResult = true
        val actualResult = response.success
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun editRecipe() = runBlocking {
        userRepository = UserRepository()
        productRepository = ProductRepository()
        val accessToken = "Bearer " + userRepository.loginUser(JAYAS).accessToken
        ServiceBuilder.token=accessToken
        val response = productRepository.updateRecipeWithoutImage("61de84132470bc2880e00c75",
            Recipe(title = "Title test", description = "Description test")
        )
        val expectedResult = true
        val actualResult = response.success
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun editPost() = runBlocking {
        userRepository = UserRepository()
        postRepository = PostRepository()
        val accessToken = "Bearer " + userRepository.loginUser(ADITI).accessToken
        ServiceBuilder.token=accessToken
        val response = postRepository.updatePostWithoutImage("61e27a66755f32c5f3345fed",
            Post(status = "Edit Post test")
        )
        val expectedResult = true
        val actualResult = response.success
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun updateReviewRating() = runBlocking {
        reviewRatingRepository = ReviewRatingRepository()
        userRepository = UserRepository()
        val accessToken = "Bearer " + userRepository.loginUser(HIRA).accessToken
        ServiceBuilder.token=accessToken
        val response = reviewRatingRepository.updateReviewRating("61e6bfcfe5805b8728bb70da",
            ReviewRating(_id="61e5873ce33af34f3b8b96f1",review = "This is updated review test", rating = 5)
        );
        val expectedResult = true
        val actualResult = response.success!!
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun deleteReviewRating() = runBlocking {
        reviewRatingRepository = ReviewRatingRepository()
        userRepository = UserRepository()
        val accessToken = "Bearer " + userRepository.loginUser(HIRA).accessToken
        ServiceBuilder.token=accessToken
        val response = reviewRatingRepository.deleteReviewRating("61e6bfcfe5805b8728bb70da","61e5873ce33af34f3b8b96f1");
        val expectedResult = true
        val actualResult = response.success!!
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun archivedRecipe() = runBlocking {
        userRepository = UserRepository()
        productRepository = ProductRepository()
        val accessToken = "Bearer " + userRepository.loginUser(ARBIN).accessToken
        ServiceBuilder.token=accessToken
        val response = productRepository.archivedRecipe("61e9422f15c0b306e1bf741d")
        val expectedResult = true
        val actualResult = response.success
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun viewArchivedRecipe() = runBlocking {
        userRepository = UserRepository()
        productRepository = ProductRepository()
        val accessToken = "Bearer " + userRepository.loginUser(ADITI).accessToken
        ServiceBuilder.token=accessToken
        val response = productRepository.viewArchivedRecipe()
        val expectedResult = true
        val actualResult = response.data!!.isNotEmpty()
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun viewArchivedPost() = runBlocking {
        userRepository = UserRepository()
        postRepository = PostRepository()
        val accessToken = "Bearer " + userRepository.loginUser(ADITI).accessToken
        ServiceBuilder.token=accessToken
        val response = postRepository.viewArchivedPost()
        val expectedResult = true
        val actualResult = response.data!!.isNotEmpty()
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun archivedPost() = runBlocking {
        userRepository = UserRepository()
        postRepository = PostRepository()
        val accessToken = "Bearer " + userRepository.loginUser(HIRA).accessToken
        ServiceBuilder.token=accessToken
        val response = postRepository.archivePost("61e9228a7252e560ba5125cc")
        val expectedResult = true
        val actualResult = response.success
        assertEquals(expectedResult, actualResult)
    }
    @Test
    fun deleteComment() = runBlocking {
        commentRepository = CommentRepository()
        userRepository = UserRepository()
        val accessToken = "Bearer " + userRepository.loginUser(JAYAS).accessToken
        ServiceBuilder.token=accessToken
        val response = commentRepository.deleteComment("61d45c5bd60e59afdc3523ae")
        val expectedResult = true
        val actualResult = response.success!!
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun reportRecipe() = runBlocking {
        userRepository = UserRepository()
        productRepository = ProductRepository()
        val accessToken = "Bearer " + userRepository.loginUser(HIRA).accessToken
        ServiceBuilder.token=accessToken
        val response = productRepository.reportRecipe("61f02fee042c1a42ccf07660","This is fake recipe")
        val expectedResult = true
        val actualResult = response.success
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun reportPost() = runBlocking {
        userRepository = UserRepository()
        postRepository = PostRepository()
        val accessToken = "Bearer " + userRepository.loginUser(RACHEL).accessToken
        ServiceBuilder.token=accessToken
        val response = postRepository.reportPost("61f14197bee1e0337866b44e","This is spam")
        val expectedResult = true
        val actualResult = response.success
        assertEquals(expectedResult, actualResult)
    }
    
    @Test
    fun deleteRecipe() = runBlocking {
        userRepository = UserRepository()
        productRepository = ProductRepository()
        val accessToken = "Bearer " + userRepository.loginUser(HIRA).accessToken
        ServiceBuilder.token=accessToken
        val response = productRepository.deleteRecipe("61f02fee042c1a42ccf07660")
        val expectedResult = true
        val actualResult = response.success
        assertEquals(expectedResult, actualResult)
    }
    @Test
    fun deletePost() = runBlocking {
        userRepository = UserRepository()
        postRepository = PostRepository()
        val accessToken = "Bearer " + userRepository.loginUser(RACHEL).accessToken
        ServiceBuilder.token=accessToken
        val response = postRepository.deletePost("61f14197bee1e0337866b44e")
        val expectedResult = true
        val actualResult = response.success
        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun updateHashtag() = runBlocking {
        productRepository = ProductRepository()
        userRepository = UserRepository()
        val accessToken = "Bearer " + userRepository.loginUser(JAYAS).accessToken
        ServiceBuilder.token=accessToken
        val recipe = Recipe(hashtag = mutableListOf("hello", "world"))
        val response = productRepository.updateRecipeHashtag("61f02fee042c1a42ccf07660", recipe)
        val expectedResult = true
        val actualResult = response.success!!
        assertEquals(expectedResult, actualResult)
    }
    @Test
    fun recentlyViewRecipe() = runBlocking {
        userRepository = UserRepository()
        val accessToken = "Bearer " + userRepository.loginUser(HIRA).accessToken
        ServiceBuilder.token=accessToken
        val response = userRepository.getUserProfile()
        val expectedResult = true
        val actualResult = response.data?.recentlyViewed?.isNotEmpty()
        assertEquals(expectedResult, actualResult)
    }
    @Test
    fun viewRestaurantDetails() = runBlocking {
        userRepository = UserRepository()
        shopRepository = ShopRepository()
        val accessToken = "Bearer " + userRepository.loginUser(RACHEL).accessToken
        ServiceBuilder.token=accessToken
        val response = shopRepository.getRestaurantById("61f14197bee1e0337866b44e")
        val expectedResult = true
        val actualResult = response.success!!
        assertEquals(expectedResult, actualResult)
    }
    @Test
    fun deleteRestaurant() = runBlocking {
        userRepository = UserRepository()
        shopRepository = ShopRepository()
        val accessToken = "Bearer " + userRepository.loginUser(RACHEL).accessToken
        ServiceBuilder.token=accessToken
        val response = shopRepository.deleteRestaurantDetails("61f02fee042c1a42ccf07660")
        val expectedResult = true
        val actualResult = response.success

    @Test
    fun getNotification() = runBlocking {
        userRepository = UserRepository()
        notificationRepository = NotificationRepository()
        val accessToken = "Bearer " + userRepository.loginUser(JAYAS).accessToken
        ServiceBuilder.token=accessToken
        val response = notificationRepository.getNotification()
        val expectedResult = true
        val actualResult = response.data!!.isNotEmpty()
        assertEquals(expectedResult, actualResult)
    }
}
