package com.ddliang.backend.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品详情响应DTO
 */
public class ProductDetailResponse {
    private Integer id;
    private String name;
    private String subtitle;
    private String brand;
    private BigDecimal rating;
    private Integer reviewCount;
    private Integer monthlySales;
    private List<String> images;
    private List<SpecificationDTO> specifications;
    private Integer stock;
    private Integer maxPurchase;
    private List<String> promotions;
    private String description;
    private String shortDescription;
    private Map<String, String> params;
    private List<RatingDistributionDTO> ratingDistribution;
    private List<ReviewDTO> reviews;
    private List<FaqDTO> faqs;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Integer getMonthlySales() {
        return monthlySales;
    }

    public void setMonthlySales(Integer monthlySales) {
        this.monthlySales = monthlySales;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<SpecificationDTO> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(List<SpecificationDTO> specifications) {
        this.specifications = specifications;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getMaxPurchase() {
        return maxPurchase;
    }

    public void setMaxPurchase(Integer maxPurchase) {
        this.maxPurchase = maxPurchase;
    }

    public List<String> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<String> promotions) {
        this.promotions = promotions;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public List<RatingDistributionDTO> getRatingDistribution() {
        return ratingDistribution;
    }

    public void setRatingDistribution(List<RatingDistributionDTO> ratingDistribution) {
        this.ratingDistribution = ratingDistribution;
    }

    public List<ReviewDTO> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewDTO> reviews) {
        this.reviews = reviews;
    }

    public List<FaqDTO> getFaqs() {
        return faqs;
    }

    public void setFaqs(List<FaqDTO> faqs) {
        this.faqs = faqs;
    }

    /**
     * 规格DTO
     */
    public static class SpecificationDTO {
        private Integer id;
        private String name;
        private BigDecimal price;
        private BigDecimal originalPrice;
        private String tag;
        private String tagColor;

        // Getters and Setters
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public BigDecimal getOriginalPrice() {
            return originalPrice;
        }

        public void setOriginalPrice(BigDecimal originalPrice) {
            this.originalPrice = originalPrice;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getTagColor() {
            return tagColor;
        }

        public void setTagColor(String tagColor) {
            this.tagColor = tagColor;
        }
    }

    /**
     * 评价分布DTO
     */
    public static class RatingDistributionDTO {
        private Integer star;
        private Integer count;
        private BigDecimal percentage;

        public Integer getStar() {
            return star;
        }

        public void setStar(Integer star) {
            this.star = star;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public BigDecimal getPercentage() {
            return percentage;
        }

        public void setPercentage(BigDecimal percentage) {
            this.percentage = percentage;
        }
    }

    /**
     * 评价DTO
     */
    public static class ReviewDTO {
        private Integer id;
        private String userName;
        private Integer rating;
        private String content;
        private String date;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

    /**
     * 常见问题DTO
     */
    public static class FaqDTO {
        private String question;
        private String answer;

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }
}
