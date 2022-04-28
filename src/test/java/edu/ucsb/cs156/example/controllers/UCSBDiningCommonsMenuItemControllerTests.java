package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBDiningCommonsMenuItem;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsMenuItemRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UCSBDiningCommonsMenuItemController.class)
@Import(TestConfig.class)
public class UCSBDiningCommonsMenuItemControllerTests extends ControllerTestCase {
    @MockBean
    UCSBDiningCommonsMenuItemRepository ucsbDiningCommonsMenuItemRepository;

    @MockBean
    UserRepository userRepository;

    // Authorization tests for /api/ucsbdiningcommons/admin/all

    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
            mockMvc.perform(get("/api/ucsbdiningcommonsmenuitem/all"))
                            .andExpect(status().is(403)); // logged out users can't get all
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
            mockMvc.perform(get("/api/ucsbdiningcommonsmenuitem/all"))
                            .andExpect(status().is(200)); // logged
    }

    @Test
    public void logged_out_users_cannot_get_by_id() throws Exception {
            mockMvc.perform(get("/api/ucsbdiningcommonsmenuitem?id=1"))
                            .andExpect(status().is(403)); // logged out users can't get by id
    }

    // Authorization tests for /api/ucsbdiningcommons/post
    // (Perhaps should also have these for put and delete)

    @Test
    public void logged_out_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/ucsbdiningcommonsmenuitem/post"))
                            .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/ucsbdiningcommonsmenuitem/post"))
                            .andExpect(status().is(403)); // only admins can post
    }

    // Tests with mocks for database actions

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

            // arrange

            UCSBDiningCommonsMenuItem item = UCSBDiningCommonsMenuItem.builder()
                            .diningCommonsCode("ortega")
                            .name("Baked Pesto Pasta with Chicken")
                            .station("Entree Specials")
                            .build();

            when(ucsbDiningCommonsMenuItemRepository.findById(eq(1L))).thenReturn(Optional.of(item));

            // act
            MvcResult response = mockMvc.perform(get("/api/ucsbdiningcommonsmenuitem?id=1"))
                            .andExpect(status().isOk()).andReturn();

            // assert

            verify(ucsbDiningCommonsMenuItemRepository, times(1)).findById(eq(1L));
            String expectedJson = mapper.writeValueAsString(item);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }
    @WithMockUser(roles = { "USER" })
        @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

            // arrange

            when(ucsbDiningCommonsMenuItemRepository.findById(eq(13L))).thenReturn(Optional.empty());

            // act
            MvcResult response = mockMvc.perform(get("/api/ucsbdiningcommonsmenuitem?id=13"))
                            .andExpect(status().isNotFound()).andReturn();

            // assert

            verify(ucsbDiningCommonsMenuItemRepository, times(1)).findById(eq(13L));
            Map<String, Object> json = responseToJson(response);
            assertEquals("EntityNotFoundException", json.get("type"));
            assertEquals("UCSBDiningCommonsMenuItem with id 13 not found", json.get("message"));
    }
        
    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_all_ucsbdiningcommonsmenuitem() throws Exception {

            // arrange

            UCSBDiningCommonsMenuItem chicken = UCSBDiningCommonsMenuItem.builder()
                            .diningCommonsCode("ortega")
                            .name("Baked Pesto Pasta with Chicken")
                            .station("Entree Specials")
                            .build();

            UCSBDiningCommonsMenuItem tofu = UCSBDiningCommonsMenuItem.builder()
                            .diningCommonsCode("ortega")
                            .name("Tofu Banh Mi Sandwich (v)")
                            .station("Entree Specials")
                            .build();

            ArrayList<UCSBDiningCommonsMenuItem> expectedItem = new ArrayList<>();
            expectedItem.addAll(Arrays.asList(chicken, tofu));

            when(ucsbDiningCommonsMenuItemRepository.findAll()).thenReturn(expectedItem);

            // act
            MvcResult response = mockMvc.perform(get("/api/ucsbdiningcommonsmenuitem/all"))
                            .andExpect(status().isOk()).andReturn();

            // assert

            verify(ucsbDiningCommonsMenuItemRepository, times(1)).findAll();
            String expectedJson = mapper.writeValueAsString(expectedItem);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void an_admin_user_can_post_a_new_item() throws Exception {
            // arrange

            UCSBDiningCommonsMenuItem salad = UCSBDiningCommonsMenuItem.builder()
                            .diningCommonsCode("ortega")
                            .name("Chicken-Caesar-Salad")
                            .station("Entrees")
                            .build();

            when(ucsbDiningCommonsMenuItemRepository.save(eq(salad))).thenReturn(salad);

            // act
            MvcResult response = mockMvc.perform(
                            post("/api/ucsbdiningcommonsmenuitem/post?diningCommonsCode=ortega&name=Chicken-Caesar-Salad&station=Entrees")
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(ucsbDiningCommonsMenuItemRepository, times(1)).save(salad);
            String expectedJson = mapper.writeValueAsString(salad);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }
    
    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_delete_an_item() throws Exception {
            // arrange

            UCSBDiningCommonsMenuItem soup = UCSBDiningCommonsMenuItem.builder()
                            .diningCommonsCode("portola")
                            .name("Cream of Broccoli Soup (v)")
                            .station("Greens & Grains")
                            .build();

            when(ucsbDiningCommonsMenuItemRepository.findById(eq(1L))).thenReturn(Optional.of(soup));

            // act
            MvcResult response = mockMvc.perform(
                            delete("/api/ucsbdiningcommonsmenuitem?id=1")
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(ucsbDiningCommonsMenuItemRepository, times(1)).findById(1L);
            verify(ucsbDiningCommonsMenuItemRepository, times(1)).delete(any());

            Map<String, Object> json = responseToJson(response);
            assertEquals("UCSBDiningCommonsMenuItem with id 1 deleted", json.get("message"));
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_tries_to_delete_non_existant_item_and_gets_right_error_message()
                    throws Exception {
            // arrange

            when(ucsbDiningCommonsMenuItemRepository.findById(eq(7L))).thenReturn(Optional.empty());

            // act
            MvcResult response = mockMvc.perform(
                            delete("/api/ucsbdiningcommonsmenuitem?id=7")
                                            .with(csrf()))
                            .andExpect(status().isNotFound()).andReturn();

            // assert
            verify(ucsbDiningCommonsMenuItemRepository, times(1)).findById(7L);
            Map<String, Object> json = responseToJson(response);
            assertEquals("UCSBDiningCommonsMenuItem with id 7 not found", json.get("message"));
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_edit_an_existing_item() throws Exception {
            // arrange

            UCSBDiningCommonsMenuItem chickenOrig = UCSBDiningCommonsMenuItem.builder()
                            .diningCommonsCode("ortega")
                            .name("Baked Pesto Pasta with Chicken")
                            .station("Entree Specials")
                            .build();

            UCSBDiningCommonsMenuItem chickenEdited = UCSBDiningCommonsMenuItem.builder()
                            .diningCommonsCode("carrillo")
                            .name("Fried Chicken")
                            .station("Entrees")
                            .build();

            String requestBody = mapper.writeValueAsString(chickenEdited);

            when(ucsbDiningCommonsMenuItemRepository.findById(eq(1L))).thenReturn(Optional.of(chickenOrig));

            // act
            MvcResult response = mockMvc.perform(
                            put("/api/ucsbdiningcommonsmenuitem?id=1")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .characterEncoding("utf-8")
                                            .content(requestBody)
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(ucsbDiningCommonsMenuItemRepository, times(1)).findById(1L);
            verify(ucsbDiningCommonsMenuItemRepository, times(1)).save(chickenEdited); // should be saved with updated info
            String responseString = response.getResponse().getContentAsString();
            assertEquals(requestBody, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_cannot_edit_item_that_does_not_exist() throws Exception {
            // arrange

            UCSBDiningCommonsMenuItem editedItem = UCSBDiningCommonsMenuItem.builder()
                            .diningCommonsCode("dlg")
                            .name("Ice Cream Sandwich")
                            .station("Desserts")
                            .build();

            String requestBody = mapper.writeValueAsString(editedItem);

            when(ucsbDiningCommonsMenuItemRepository.findById(eq(3L))).thenReturn(Optional.empty());

            // act
            MvcResult response = mockMvc.perform(
                            put("/api/ucsbdiningcommonsmenuitem?id=3")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .characterEncoding("utf-8")
                                            .content(requestBody)
                                            .with(csrf()))
                            .andExpect(status().isNotFound()).andReturn();

            // assert
            verify(ucsbDiningCommonsMenuItemRepository, times(1)).findById(3L);
            Map<String, Object> json = responseToJson(response);
            assertEquals("UCSBDiningCommonsMenuItem with id 3 not found", json.get("message"));

    }

}
