package talky.dietcontrol.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import talky.dietcontrol.model.entities.Category;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, Long> {
    List<Category> findAll();

    @Modifying
    @Query(value = """
            DELETE FROM categories_distribution
            WHERE  category_id = :categoryId AND recipe_id = :recipeId
            """, nativeQuery = true)
    void deleteByCategoryRecipeId(Long categoryId, Long recipeId);

    @Modifying()
    @Query(value = """
            INSERT INTO categories_distribution(category_id, recipe_id) VALUES (:categoryId, :recipeId);
            """, nativeQuery = true)
    void addRecipeToCategory(Long recipeId, Long categoryId);

    @Query(value = """
              SELECT categories.* FROM categories
              JOIN categories_distribution distr ON categories.id = distr.category_id
              WHERE distr.recipe_id = :id
            """, nativeQuery = true)
    List<Category> findByRecipeId(Long id);

    @Query(value = """
            SELECT categories.* FROM categories
            JOIN selections_distribution distr ON categories.id = distr.category_id
            WHERE distr.selection_id = :id
            """, nativeQuery = true)
    List<Category> findBySelectionId(Long id);

}
