package com.ecom.backend.service;

import com.ecom.backend.exceptions.ResourceNotFoundException;
import com.ecom.backend.mapper.ProductMapper;
import com.ecom.backend.model.Category;
import com.ecom.backend.model.Product;
import com.ecom.backend.payload.ProductDTO;
import com.ecom.backend.payload.ProductResponseDTO;
import com.ecom.backend.repository.CategoryRepository;
import com.ecom.backend.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductDTO addProduct(Product product, Long categoryId)
    {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category","categoryId",categoryId));
        setProductAttributes(product,category,"default.png");
        productRepository.save(product);

        return productMapper.toDTO(product);
    }

    @Override
    public ProductResponseDTO getAllProducts() {
        List<Product> products = productRepository.findAll();
        return new ProductResponseDTO(productMapper.toDTOList(products));
    }

    @Override
    public ProductResponseDTO getProductsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category","categoryId",categoryId));

        List<Product> products = productRepository.findByCategory(category);
        return new ProductResponseDTO(productMapper.toDTOList(products));
    }


    private void setProductAttributes(Product product,Category category,String image)
    {
        product.setCategory(category);
        product.setImage(image);
        product.setSpecialPrice(product.getPrice()*(1-(product.getDiscount()/100)));
    }
}
