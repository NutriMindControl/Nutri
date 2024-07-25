package talky.dietcontrol.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import talky.dietcontrol.exceptions.BadRequestException;
import talky.dietcontrol.model.dto.RecipeDTO;
import talky.dietcontrol.model.entities.Diagnosis;
import talky.dietcontrol.model.entities.ProductCategoryProhibition;
import talky.dietcontrol.model.mappers.DefaultMapper;
import talky.dietcontrol.repository.ProductCategoryProhibitionRepository;
import talky.dietcontrol.services.interfaces.DiagnosisService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class RecipeServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private DiagnosisService diagnosisService;

    @Mock
    private ProductCategoryProhibitionRepository productRepository;

    @Mock
    private DefaultMapper modelMapper;

    @InjectMocks
    private RecipeServiceImpl recipeService;

    private ObjectMapper objectMapper = new ObjectMapper();
    private Random random = new Random();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private String readJsonFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    @Test
    void testFindAllowedRecipesForDiagnose() throws IOException {
        Long diagnoseId = 1L;
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setCode(3L);

        List<ProductCategoryProhibition> notAllowedProducts = new ArrayList<>();
        ProductCategoryProhibition prohibition = new ProductCategoryProhibition();
        prohibition.setProductId(1L);
        notAllowedProducts.add(prohibition);

        String jsonResponse = readJsonFile("src/test/resources/jsons/recipes.json");

        when(diagnosisService.fetchDiagnosis(diagnoseId)).thenReturn(diagnosis);
        when(productRepository.findByCategoryNumber(diagnosis.getCode())).thenReturn(notAllowedProducts);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(jsonResponse, HttpStatus.OK));
        LogCaptor logCaptor = LogCaptor.forClass(RecipeServiceImpl.class);

        List<RecipeDTO> recipes = recipeService.findAllowedRecipesForDiagnose(diagnoseId);

        assertNotNull(recipes);
        assertEquals(1, recipes.size());
        assertEquals("Запеканка с булгуром и индейкой", recipes.get(0).getRecipeName());

        List<String> logs = logCaptor.getInfoLogs();
        assertTrue(logs.stream().anyMatch(log -> log.contains("Processing getting recipes from TalkyChef which have products allowed for diagnosis with id: 1")));
        assertTrue(logs.stream().anyMatch(log -> log.contains("Got recipes for diagnose with id: 1")));

    }

    @Test
    void testFindAllowedRecipesForDiagnose_NotFound() {
        Long diagnoseId = 1L;
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setCode(3L);

        when(diagnosisService.fetchDiagnosis(diagnoseId)).thenReturn(diagnosis);

        assertThrows(NullPointerException.class, () -> {
            recipeService.findAllowedRecipesForDiagnose(diagnoseId);
        });

    }

    @Test
    void testFindAllowedRecipesForDiagnose_StatusNotOK() {
        Long diagnoseId = 1L;
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setCode(2L);

        List<ProductCategoryProhibition> notAllowedProducts = new ArrayList<>();
        ProductCategoryProhibition prohibition = new ProductCategoryProhibition();
        prohibition.setProductId(1L);
        notAllowedProducts.add(prohibition);

        when(diagnosisService.fetchDiagnosis(diagnoseId)).thenReturn(diagnosis);
        when(productRepository.findByCategoryNumber(diagnosis.getCode())).thenReturn(notAllowedProducts);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));

        LogCaptor logCaptor = LogCaptor.forClass(BadRequestException.class);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            recipeService.findAllowedRecipesForDiagnose(diagnoseId);
        });

        assertEquals("Failed to fetch recipes. Status code: 400 BAD_REQUEST", exception.getMessage());

        List<String> logs = logCaptor.getErrorLogs();
        assertTrue(logs.stream().anyMatch(log -> log.contains("Failed to process query. Status code: 400 BAD_REQUEST")));
    }
}
