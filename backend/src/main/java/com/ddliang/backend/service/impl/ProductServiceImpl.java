package com.ddliang.backend.service.impl;

import com.ddliang.backend.common.Result;
import com.ddliang.backend.dto.ProductDetailResponse;
import com.ddliang.backend.dto.ProductResponse;
import com.ddliang.backend.entity.*;
import com.ddliang.backend.mapper.ProductMapper;
import com.ddliang.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品服务实现类
 */
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<ProductResponse> getProductsByCategory(String category, Integer limit) {
        List<Product> products;

        // 根据分类查询商品
        switch (category.toLowerCase()) {
            case "hot":
                // 热门推荐
                products = productMapper.findHotProducts(limit);
                break;
            case "food":
                // 宠物食品 (category_id = 1)
                products = productMapper.findByCategoryId(1, limit);
                break;
            case "supply":
                // 宠物用品 (category_id = 2)
                products = productMapper.findByCategoryId(2, limit);
                break;
            case "toy":
                // 宠物玩具 (category_id = 3)
                products = productMapper.findByCategoryId(3, limit);
                break;
            default:
                throw new IllegalArgumentException("无效的分类参数: " + category);
        }

        // 转换为DTO
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDetailResponse getProductDetail(Integer id) {
        Product product = productMapper.findById(id);
        if (product == null) {
            throw new IllegalArgumentException("商品不存在: " + id);
        }

        return convertToDetailResponse(product);
    }

    @Override
    public List<ProductResponse> getRecommendedProducts(Integer productId, Integer limit) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            return new ArrayList<>();
        }

        List<Product> products = productMapper.findRecommendedProducts(
                product.getCategoryId(), 
                productId, 
                limit
        );

        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> searchProducts(String keyword, Integer page, Integer pageSize) {
        // 计算偏移量
        int offset = (page - 1) * pageSize;
        
        // 搜索商品
        List<Product> products = productMapper.searchProducts(keyword, offset, pageSize);
        
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getAllProducts(Integer page, Integer pageSize) {
        // 计算偏移量
        int offset = (page - 1) * pageSize;
        
        // 获取所有商品
        List<Product> products = productMapper.findAllProducts(offset, pageSize);
        
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Result<?> advancedSearch(String keyword, String category, Double minPrice, 
                                   Double maxPrice, String sortBy, Integer page, Integer pageSize) {
        try {
            // 计算偏移量
            int offset = (page - 1) * pageSize;
            
            // 转换分类为category_id
            Integer categoryId = null;
            if (category != null && !category.isEmpty()) {
                switch (category.toLowerCase()) {
                    case "food":
                        categoryId = 1;
                        break;
                    case "supply":
                        categoryId = 2;
                        break;
                    case "toy":
                        categoryId = 3;
                        break;
                }
            }
            
            // 执行高级搜索
            List<Product> products = productMapper.advancedSearch(
                keyword, categoryId, minPrice, maxPrice, sortBy, offset, pageSize
            );
            
            // 获取总数
            int total = productMapper.countAdvancedSearch(keyword, categoryId, minPrice, maxPrice);
            
            // 转换为DTO
            List<ProductResponse> productResponses = products.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("list", productResponses);
            result.put("total", total);
            result.put("page", page);
            result.put("pageSize", pageSize);
            result.put("totalPages", (int) Math.ceil((double) total / pageSize));
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("搜索失败: " + e.getMessage());
        }
    }

    /**
     * 将Product实体转换为ProductResponse DTO
     */
    private ProductResponse convertToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setBrand(product.getBrand());
        response.setPrice(product.getPrice());
        response.setOriginalPrice(product.getOriginalPrice());
        response.setRating(product.getRating());
        response.setRatingCount(product.getRatingCount());
        response.setImage(product.getImageUrl());
        response.setTag(product.getTag());
        response.setSales(product.getSales());
        return response;
    }

    /**
     * 将Product实体转换为ProductDetailResponse DTO
     */
    private ProductDetailResponse convertToDetailResponse(Product product) {
        ProductDetailResponse response = new ProductDetailResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setSubtitle(product.getShortDescription());
        response.setBrand(product.getBrand());
        response.setRating(product.getRating());
        response.setReviewCount(product.getRatingCount());
        response.setMonthlySales(product.getSales());
        response.setStock(product.getStock());
        response.setMaxPurchase(10); // 默认限购10件
        response.setDescription(product.getDescription());
        response.setShortDescription(product.getShortDescription());

        // 设置图片列表(主图)
        List<String> images = new ArrayList<>();
        if (product.getImageUrl() != null) {
            images.add(product.getImageUrl());
        }
        response.setImages(images);

        // 查询并设置规格
        try {
            List<ProductSpecification> specifications = productMapper.findSpecificationsByProductId(product.getId());
            if (specifications != null && !specifications.isEmpty()) {
                List<ProductDetailResponse.SpecificationDTO> specDTOs = specifications.stream()
                        .map(spec -> {
                            ProductDetailResponse.SpecificationDTO dto = new ProductDetailResponse.SpecificationDTO();
                            dto.setId(spec.getId());
                            dto.setName(spec.getSpecName());
                            dto.setPrice(spec.getPrice());
                            dto.setOriginalPrice(spec.getOriginalPrice());
                            dto.setTag(spec.getTag());
                            dto.setTagColor(spec.getTagColor());
                            return dto;
                        })
                        .collect(Collectors.toList());
                response.setSpecifications(specDTOs);
            } else {
                setDefaultSpecification(response, product);
            }
        } catch (Exception e) {
            // 如果规格表不存在或查询失败，使用默认规格
            setDefaultSpecification(response, product);
        }

        // 查询并设置商品参数
        try {
            List<ProductParam> params = productMapper.findParamsByProductId(product.getId());
            if (params != null && !params.isEmpty()) {
                Map<String, String> paramsMap = new LinkedHashMap<>();
                for (ProductParam param : params) {
                    paramsMap.put(param.getParamName(), param.getParamValue());
                }
                response.setParams(paramsMap);
            } else {
                response.setParams(new HashMap<>());
            }
        } catch (Exception e) {
            response.setParams(new HashMap<>());
        }

        // 查询并设置评价分布
        try {
            List<ProductRatingDistribution> ratingDistributions = productMapper.findRatingDistributionByProductId(product.getId());
            if (ratingDistributions != null && !ratingDistributions.isEmpty()) {
                List<ProductDetailResponse.RatingDistributionDTO> distDTOs = ratingDistributions.stream()
                        .map(dist -> {
                            ProductDetailResponse.RatingDistributionDTO dto = new ProductDetailResponse.RatingDistributionDTO();
                            dto.setStar(dist.getStar());
                            dto.setCount(dist.getCount());
                            dto.setPercentage(dist.getPercentage());
                            return dto;
                        })
                        .collect(Collectors.toList());
                response.setRatingDistribution(distDTOs);
            } else {
                response.setRatingDistribution(new ArrayList<>());
            }
        } catch (Exception e) {
            response.setRatingDistribution(new ArrayList<>());
        }

        // 查询并设置评价列表（最多5条）
        try {
            List<ProductReview> reviews = productMapper.findReviewsByProductId(product.getId(), 5);
            if (reviews != null && !reviews.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                List<ProductDetailResponse.ReviewDTO> reviewDTOs = reviews.stream()
                        .map(review -> {
                            ProductDetailResponse.ReviewDTO dto = new ProductDetailResponse.ReviewDTO();
                            dto.setId(review.getId());
                            dto.setUserName(review.getIsAnonymous() ? "匿名用户" : "用户" + review.getUserId());
                            dto.setRating(review.getRating());
                            dto.setContent(review.getContent());
                            dto.setDate(review.getCreatedAt().format(formatter));
                            return dto;
                        })
                        .collect(Collectors.toList());
                response.setReviews(reviewDTOs);
            } else {
                response.setReviews(new ArrayList<>());
            }
        } catch (Exception e) {
            // 如果评价表不存在或查询失败，返回空列表
            response.setReviews(new ArrayList<>());
        }

        // 查询并设置常见问题
        try {
            List<ProductFaq> faqs = productMapper.findFaqsByProductId(product.getId());
            if (faqs != null && !faqs.isEmpty()) {
                List<ProductDetailResponse.FaqDTO> faqDTOs = faqs.stream()
                        .map(faq -> {
                            ProductDetailResponse.FaqDTO dto = new ProductDetailResponse.FaqDTO();
                            dto.setQuestion(faq.getQuestion());
                            dto.setAnswer(faq.getAnswer());
                            return dto;
                        })
                        .collect(Collectors.toList());
                response.setFaqs(faqDTOs);
            } else {
                response.setFaqs(new ArrayList<>());
            }
        } catch (Exception e) {
            response.setFaqs(new ArrayList<>());
        }

        // 设置促销信息
        List<String> promotions = new ArrayList<>();
        if (product.getDiscount() != null && product.getDiscount() > 0) {
            promotions.add("限时折扣 " + product.getDiscount() + "% OFF");
        }
        promotions.add("满99元包邮");
        promotions.add("新用户首单立减10元");
        response.setPromotions(promotions);

        return response;
    }

    /**
     * 设置默认规格
     */
    private void setDefaultSpecification(ProductDetailResponse response, Product product) {
        List<ProductDetailResponse.SpecificationDTO> defaultSpecs = new ArrayList<>();
        ProductDetailResponse.SpecificationDTO spec = new ProductDetailResponse.SpecificationDTO();
        spec.setId(1);
        spec.setName("标准装");
        spec.setPrice(product.getPrice());
        spec.setOriginalPrice(product.getOriginalPrice());
        spec.setTag(product.getTag());
        spec.setTagColor("#ff6b6b");
        defaultSpecs.add(spec);
        response.setSpecifications(defaultSpecs);
    }
}
