package com.ecom.backend.service;

import com.ecom.backend.exceptions.GenericAPIException;
import com.ecom.backend.exceptions.ResourceAlreadyExistsException;
import com.ecom.backend.exceptions.ResourceNotFoundException;
import com.ecom.backend.mapper.ProductMapper;
import com.ecom.backend.model.Category;
import com.ecom.backend.model.Product;
import com.ecom.backend.payload.ProductDTO;
import com.ecom.backend.payload.ProductResponseDTO;
import com.ecom.backend.repository.CartItemRepository;
import com.ecom.backend.repository.CategoryRepository;
import com.ecom.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CartItemRepository cartItemRepository;

    @Value("${project.image}")
    private String path;

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId)
    {
        if(productRepository.existsByProductName(productDTO.getProductName()))
        {
            throw new ResourceAlreadyExistsException("Product","productName",productDTO.getProductName());
        }
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category","categoryId",categoryId));

        Product product = productMapper.toModel(productDTO);
        setProductAttributes(product,category,"default.png");
        product.setIsActive(true);
        productRepository.save(product);

        return productMapper.toDTO(product);
    }

    @Transactional(readOnly = true)
    @Override
    public ProductResponseDTO getAllProducts(Integer pageNum, Integer pageSize, String sortBy, String sortDir) {

        Sort sortByAndOrder = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNum,pageSize,sortByAndOrder);
        Page<Product> pageProduct = productRepository.findAll(pageDetails);
        List<Product> products = pageProduct.getContent();

        if(products.isEmpty())
        {
            throw new GenericAPIException("No products found.");
        }
        ProductResponseDTO responseDTO = new ProductResponseDTO();
        responseDTO.setContent(productMapper.toDTOList(products));
        responseDTO.setTotalPages(pageProduct.getTotalPages());
        responseDTO.setLastPage(pageProduct.isLast());
        responseDTO.setPageNum(pageProduct.getNumber());
        responseDTO.setPageSize(pageProduct.getSize());
        responseDTO.setTotalElements(pageProduct.getTotalElements());

        return responseDTO;
    }

    @Override
    public ProductResponseDTO getProductsByCategory(Long categoryId,Integer pageNum, Integer pageSize, String sortBy, String sortDir) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category","categoryId",categoryId));

        Sort sortByAndOrder = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNum,pageSize,sortByAndOrder);
        Page<Product> pageProduct = productRepository.findByCategory(category,pageDetails);

        List<Product> products = pageProduct.getContent();

        if(products.isEmpty())
        {
            throw new GenericAPIException("No products found.");
        }

        ProductResponseDTO responseDTO = new ProductResponseDTO();
        responseDTO.setContent(productMapper.toDTOList(products));
        responseDTO.setTotalPages(pageProduct.getTotalPages());
        responseDTO.setLastPage(pageProduct.isLast());
        responseDTO.setPageNum(pageProduct.getNumber());
        responseDTO.setPageSize(pageProduct.getSize());
        responseDTO.setTotalElements(pageProduct.getTotalElements());

        return responseDTO;
    }

    @Override
    public ProductResponseDTO getProductsByKeyword(String keyword,Integer pageNum, Integer pageSize, String sortBy, String sortDir) {

        Sort sortByAndOrder = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNum,pageSize,sortByAndOrder);
        Page<Product> pageProduct = productRepository.findByProductNameContainingIgnoreCase(keyword,pageDetails);

        List<Product> products = pageProduct.getContent();

        if(products.isEmpty())
        {
            throw new GenericAPIException("No products found.");
        }
        ProductResponseDTO responseDTO = new ProductResponseDTO();
        responseDTO.setContent(productMapper.toDTOList(products));
        responseDTO.setTotalPages(pageProduct.getTotalPages());
        responseDTO.setLastPage(pageProduct.isLast());
        responseDTO.setPageNum(pageProduct.getNumber());
        responseDTO.setPageSize(pageProduct.getSize());
        responseDTO.setTotalElements(pageProduct.getTotalElements());

        return responseDTO;
    }


    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO entity) {

        Product product = productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product","productId",productId));
        updateProductAttributes(product,entity);
        productRepository.save(product);
        return productMapper.toDTO(product);
    }

    @Transactional
    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product","productId",productId));
//        productRepository.delete(product);
        cartItemRepository.deleteByProductId(productId);
        product.setIsActive(false);
        return productMapper.toDTO(productRepository.save(product));
    }

    @Override
    public ProductDTO updateImage(Long productId, MultipartFile image) throws IOException {

        Product product = productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product","productId",productId));


        String fileName = uploadImage(image);

        product.setImage(fileName);
        productRepository.save(product);

        return productMapper.toDTO(product);
    }

    // helper ->


    private String uploadImage(MultipartFile image) throws IOException {

        if(image.isEmpty())
        {
            throw new ResourceNotFoundException("File","Image","[]");
        }

        File folder = new File(path);
        if(!folder.exists())
        {
            boolean created = folder.mkdir();
        }

        StringBuilder filePath = new StringBuilder();
        String fileName = UUID.randomUUID().toString() + image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf('.'));
        filePath.append(path).append(File.separator).append(fileName);

        Files.copy(image.getInputStream(),Paths.get(filePath.toString()));

        return fileName;
    }


    private void updateProductAttributes(Product product,ProductDTO entity)
    {
        product.setProductName(entity.getProductName());
        product.setDescription(entity.getDescription());
        product.setQuantity(entity.getQuantity());
        product.setPrice(entity.getPrice());
        product.setDiscount(entity.getDiscount());
        product.setSpecialPrice(entity.getPrice() * (1 - (entity.getDiscount()/100)));
    }

    private void setProductAttributes(Product product, Category category, String image)
    {
        product.setCategory(category);
        product.setImage(image);
        product.setSpecialPrice(product.getPrice()*(1-(product.getDiscount()/100)));
    }
}
