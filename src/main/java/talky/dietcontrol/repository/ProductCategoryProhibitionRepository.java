package talky.dietcontrol.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import talky.dietcontrol.model.entities.ProductCategoryProhibition;

import java.util.List;

public interface ProductCategoryProhibitionRepository extends JpaRepository<ProductCategoryProhibition, Long> {
    List<ProductCategoryProhibition> findByCategoryNumber(Long diagnoseId);
}