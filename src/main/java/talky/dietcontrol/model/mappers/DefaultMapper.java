package talky.dietcontrol.model.mappers;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class DefaultMapper extends ModelMapper {
    public DefaultMapper() {
        super();


        Converter<String, String> toLowercase = ctx -> ctx.getSource() == null ? null : ctx.getSource().toLowerCase();

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
