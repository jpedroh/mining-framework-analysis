package org.mybatis.jpetstore.mapper;

import java.util.List;
import org.mybatis.jpetstore.domain.Item;
import org.mybatis.jpetstore.domain.Product;
import org.mybatis.jpetstore.domain.Review;
import org.mybatis.jpetstore.domain.ReviewRating;

public interface ReviewMapper {
    List<Review> getReviews();
    Review getReviewById(String id);
    void deleteReviewById(String id);
<<<<<<< /usr/src/app/output/mybatis/jpetstore-6/b26b0c87017c0f2ec431ee0a1425910b9a9fff00/src/main/java/org/mybatis/jpetstore/mapper/ReviewMapper.java/left.java
    Item getItemById(String id);
    Product getProductById(String id);
    void insertReview(Review review);
    void insertReviewRating(ReviewRating rr);
||||||| /usr/src/app/output/mybatis/jpetstore-6/b26b0c87017c0f2ec431ee0a1425910b9a9fff00/src/main/java/org/mybatis/jpetstore/mapper/ReviewMapper.java/base.java
=======
    List<Review> getReivewListByProductId(String productId);
>>>>>>> /usr/src/app/output/mybatis/jpetstore-6/b26b0c87017c0f2ec431ee0a1425910b9a9fff00/src/main/java/org/mybatis/jpetstore/mapper/ReviewMapper.java/right.java
}
