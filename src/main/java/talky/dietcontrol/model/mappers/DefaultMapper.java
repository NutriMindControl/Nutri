package talky.dietcontrol.model.mappers;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Component;
import talky.dietcontrol.model.dto.recipes.IngredientsDistributionDto;
import talky.dietcontrol.model.entities.IngredientsDistribution;
import talky.dietcontrol.model.entities.Product;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class DefaultMapper extends ModelMapper {
    public DefaultMapper() {
        super();


        Converter<String, String> toLowercase = ctx -> ctx.getSource() == null ? null : ctx.getSource().toLowerCase();
        this.addMappings(new PropertyMap<IngredientsDistributionDto, IngredientsDistribution>() {
            @Override
            protected void configure() {
                // Set the Product (ingredient) based on ingredientId from the DTO
                using(ctx -> {
                    Product product = new Product();
                    product.setProductId((Long) ctx.getSource());
                    return product;
                }).map(source.getIngredientId(), destination.getIngredient());
            }

        });
        this.addMappings(new PropertyMap<IngredientsDistribution, IngredientsDistributionDto>() {
            @Override
            protected void configure() {
                // Set ingredientId in DTO from the Product entity
                map().setIngredientId(source.getIngredient().getProductId());
                map().setProductName(source.getIngredient().getProductName());
            }
        });

    }

    private static class OffsetDateTimeToLocalDateTimeConverter extends
            AbstractConverter<OffsetDateTime, LocalDateTime> {
        @Override
        protected LocalDateTime convert(OffsetDateTime source) {
            if (source != null) {
                return source.toLocalDateTime();
            }
            return null;
        }
    }

    private static class LocalDateTimeToOffsetDateTimeConverter extends AbstractConverter<LocalDateTime, OffsetDateTime> {
        @Override
        protected OffsetDateTime convert(LocalDateTime source) {
            if (source != null) {
                return source.atOffset(ZoneOffset.UTC);
            }
            return null;
        }
    }

}
