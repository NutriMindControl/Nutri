package talky.dietcontrol.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import talky.dietcontrol.config.Constants;
import talky.dietcontrol.exceptions.NotFoundException;
import talky.dietcontrol.model.dto.TalkyRecipeDTO;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DailyMenuServiceImplTest {

    @Mock
    private RestTemplate restTemplate;


    @InjectMocks
    private DailyMenuServiceImpl dailyMenuService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetRecipeById() throws JsonProcessingException {
        Long recipeId = 1L;
        String url = Constants.TALKY_URL + "/recipes/" + recipeId;
        String jsonResponse = "{\"name\": \"Test Recipe\"}";

        when(restTemplate.getForEntity(URI.create(url), String.class))
                .thenReturn(new ResponseEntity<>(jsonResponse, HttpStatus.OK));

        TalkyRecipeDTO expectedRecipeDTO = new ObjectMapper().readValue(jsonResponse, TalkyRecipeDTO.class);

        ResponseEntity<TalkyRecipeDTO> response = dailyMenuService.getRecipeById(recipeId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedRecipeDTO.getName(), response.getBody().getName());

        verify(restTemplate, times(1)).getForEntity(URI.create(url), String.class);
    }

    @Test
    void testGetRecipeById_NotFound() throws JsonProcessingException {
        Long recipeId = 1L;
        String url = Constants.TALKY_URL + "/recipes/" + recipeId;

        when(restTemplate.getForEntity(URI.create(url), String.class))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            dailyMenuService.getRecipeById(recipeId);
        });

        assertEquals("Couldn't get recipe", exception.getMessage());

        verify(restTemplate, times(1)).getForEntity(URI.create(url), String.class);
    }
}
