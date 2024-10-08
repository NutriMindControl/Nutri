package talky.dietcontrol.services.impl;

import com.itextpdf.text.pdf.BaseFont;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;
import talky.dietcontrol.exceptions.NotFoundException;
import talky.dietcontrol.model.dto.dailymenu.DailyMenuDTO;
import talky.dietcontrol.model.dto.recipes.RecipeDTO;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfService {


    public String generateHtml(DailyMenuDTO menu, List<RecipeDTO> talkyBreakfast, List<RecipeDTO> talkyLunch, List<RecipeDTO> talkyDinner) {
        try {
            TemplateEngine templateEngine = new SpringTemplateEngine();

            ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
            templateResolver.setPrefix("templates/");
            templateResolver.setSuffix(".html");
            templateResolver.setTemplateMode("HTML");
            templateResolver.setCharacterEncoding("UTF-8");
            templateEngine.setTemplateResolver(templateResolver);

            Context context = new Context();
            context.setVariable("mealData", menu);

            context.setVariable("breakfastRecipes", talkyBreakfast);
            context.setVariable("lunchRecipes", talkyLunch);
            context.setVariable("dinnerRecipes", talkyDinner);

            return templateEngine.process("template", context);
        } catch (Exception e) {
            throw new NotFoundException("Failed to generate HTML");
        }
    }

    public byte[] generatePdfFromHtml(String html) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ITextRenderer renderer = new ITextRenderer();
            renderer.getFontResolver().addFont("templates/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            renderer.setDocumentFromString(html);

            renderer.layout();
            renderer.createPDF(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new NotFoundException("Failed to generate PDF");
        }
    }
}
