package talky.dietcontrol.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import talky.dietcontrol.model.entities.Recipe;

import java.util.List;

@Repository
public interface RecipeRepository extends CrudRepository<Recipe, Long> {

    @Query(value = """
                SELECT recipes.* FROM recipes
                JOIN categories_distribution distr ON recipes.id = distr.recipe_id
                WHERE category_id = :id
                LIMIT :limit OFFSET :limit * :page
            """, nativeQuery = true)
    List<Recipe> findByCategoryId(Long id, int limit, int page);


    @Query(value = """
            SELECT r.*
            FROM recipes r
            WHERE r.id NOT IN (
                SELECT rp.recipe_id
                FROM ingredients_distribution rp
                WHERE rp.ingredient_id IN (:forbiddenProductIds)
            AND r.id in (select cd.recipe_id from categories_distribution cd where category_id in (22,23,24))
            ORDER BY RANDOM()
            LIMIT :limit
            """, nativeQuery = true)
    List<Recipe> findRecipesNotContainingProducts(@Param("forbiddenProductIds") List<Long> forbiddenProductIds, @Param("limit") Integer limit);


    @Query(value = """
            SELECT r.* FROM recipes r
                JOIN categories_distribution distr ON r.id = distr.recipe_id
                Join public.categories c on c.id = distr.category_id
                WHERE c.name = :daytime and r.id NOT IN (
                SELECT rp.recipe_id
                FROM ingredients_distribution rp
                WHERE rp.ingredient_id IN :forbiddenProductIds)
            ORDER BY RANDOM()
            """, nativeQuery = true)
    List<Recipe> findRecipesNotContainingProductsForDaytime(@Param("forbiddenProductIds") List<Long> diagnoseId, @Param("daytime") String daytime);
}
