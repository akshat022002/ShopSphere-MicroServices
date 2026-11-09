package com.akshat.review.review.mapper;

import com.akshat.review.review.dto.ReviewResponse;
import com.akshat.review.review.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {


    @Mapping(target = "username",ignore = true)
    ReviewResponse toResponse(Review review);
}
