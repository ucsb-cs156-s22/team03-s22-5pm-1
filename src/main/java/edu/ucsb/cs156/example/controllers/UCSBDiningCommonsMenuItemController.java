package edu.ucsb.cs156.example.controllers;
import edu.ucsb.cs156.example.entities.UCSBDiningCommonsMenuItem;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsMenuItemRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import com.fasterxml.jackson.core.JsonProcessingException;

@Api(description = "UCSBDiningCommonsMenuItem")
@RequestMapping("/api/ucsbdiningcommonsmenuitem")
@RestController
@Slf4j
public class UCSBDiningCommonsMenuItemController extends ApiController {
    @Autowired
    UCSBDiningCommonsMenuItemRepository ucsbDiningCommonsMenuItemRepository;
    @ApiOperation(value = "List all ucsb dining commons menu items")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBDiningCommonsMenuItem> allMenuItems() {
        Iterable<UCSBDiningCommonsMenuItem> items = ucsbDiningCommonsMenuItemRepository.findAll();
        return items;
    }

    @ApiOperation(value = "Create a new item")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBDiningCommonsMenuItem postItem(
        @ApiParam("diningCommonsCode") @RequestParam String diningCommonsCode,
        @ApiParam("name") @RequestParam String name,
        @ApiParam("station") @RequestParam String station
        )
        {

        UCSBDiningCommonsMenuItem item = new UCSBDiningCommonsMenuItem();
        item.setDiningCommonsCode(diningCommonsCode);
        item.setName(name);
        item.setStation(station);

        UCSBDiningCommonsMenuItem savedItem = ucsbDiningCommonsMenuItemRepository.save(item);

        return savedItem;
    }

    @ApiOperation(value = "Get a single commons")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public UCSBDiningCommonsMenuItem getById(
            @ApiParam("id") @RequestParam long id) {
        UCSBDiningCommonsMenuItem item = ucsbDiningCommonsMenuItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBDiningCommonsMenuItem.class, id));

        return item;
    }
    
    @ApiOperation(value = "Update a single item")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public UCSBDiningCommonsMenuItem updateItem(
            @ApiParam("id") @RequestParam long id,
            @RequestBody @Valid UCSBDiningCommonsMenuItem incoming) {

        UCSBDiningCommonsMenuItem item = ucsbDiningCommonsMenuItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBDiningCommonsMenuItem.class, id));


        item.setDiningCommonsCode(incoming.getDiningCommonsCode());  
        item.setName(incoming.getName());
        item.setStation(incoming.getStation());

        ucsbDiningCommonsMenuItemRepository.save(item);

        return item;
    }
    
    @ApiOperation(value = "Delete a UCSBDiningCommonsMenuItem")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteItem(
            @ApiParam("id") @RequestParam long id) {
        UCSBDiningCommonsMenuItem item = ucsbDiningCommonsMenuItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBDiningCommonsMenuItem.class, id));

        ucsbDiningCommonsMenuItemRepository.delete(item);
        return genericMessage("UCSBDiningCommonsMenuItem with id %s deleted".formatted(id));
    }
}
