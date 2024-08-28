package talky.dietcontrol.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import talky.dietcontrol.model.entities.Product;

import java.util.List;

public interface ProductsRepository extends CrudRepository<Product, Long> {
    @Query(value = """
            SELECT *
            FROM products p
            WHERE p.product_id
                      IN (SELECT * FROM allowed_products) 
              AND p.product_id NOT IN (SELECT product_id 
                                       FROM product_category_prohibitions 
                                       WHERE category_number = (
                                       SELECT code 
                                       FROM diagnosis 
                                       WHERE diagnose_id = :diagnoseId 
                                       LIMIT 1)
                                       )
            """, nativeQuery = true)
    List<Product> findAllowedProducts(@Param("diagnoseId") Long diagnoseId);

}