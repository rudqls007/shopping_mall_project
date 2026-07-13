package toy.project.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import toy.project.dto.ItemFormDto;
import toy.project.dto.ItemSearchDto;
import toy.project.entity.Item;
import toy.project.service.ItemService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;


    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model) {
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "item/itemForm";
    }

    @PostMapping("/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, Model model,
                          @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList) {

        /* 상품 등록 시 필수 값이 없다면 다시 상품 등록 페이지로 전환함. */
        if (bindingResult.hasErrors()) {
            return "item/itemForm";
        }

        /* 상품 등록 시 첫 번째 이미지가 없다면 에러 메세지와 함께 상품 등록 페이지로 전환함.
         *  상품의 첫 번째 이미지는 메인 페이지에서 ㅂ여줄 상품 이미지로 사용하기 위해서 필수 값으로 지정함. */
        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute("errorMessage", "첫번쟤 상품 이미지는 필수 입력 값입니다.");
            return "item/itemForm";
        }

        try {

            /* 상품 저장 로직을 호출하여 매개 변수로 상품 정보와 상품 이미지 정보를 담고 있는 itemImgFileList를 넘겨줌 */
            itemService.saveItem(itemFormDto, itemImgFileList);


        } catch (Exception e) {
            model.addAttribute("errorMassage", "상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }
        /* 상품이 정상적으로 등록되었다면 메인 페이지로 이동 */
        return "redirect:/";
    }


    @GetMapping("/admin/item/{itemId}")
    public String itemDtl(@PathVariable Long itemId, Model model) {

        try {
            /* 조회한 상품 데이터를 모델에 담아서 뷰로 전달함. */
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
            /* 상품 엔티티가 존재하지 않을 경우 에러메세지를 담아서 상품 등록 페이지로 이동함. */
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
            model.addAttribute("itemFOrmDto", new ItemFormDto());
            return "item/itemForm";
        }

        return "item/itemForm";
    }

    @PostMapping("/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto,
                             BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList,
                             Model model) {

        if (bindingResult.hasErrors()) {
            return "item/itemForm";
        }
        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute(
                    "errorMessage", "첫번째 상품 이미지는 필수 입력 값입니다.");
            return "item/itemForm";
        }

        try {
            /* 상품 수정 로직을 호출 */
            itemService.updateItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute(
                    "errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }

        return "redirect:/";
    }

    /* 상품 관리 진입 시 URL에 페이지 번호가 없는 경우와 페이지 번호가 있는 경우 2가지를 매핑 */
    @GetMapping({"/admin/items", "admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page, Model model) {
        /* 페이징을 위해서 PageRequest.of 메소드를 통해 Pageable 객체를 생성함.
           첫 번째 파라미터로는 조회할 떄 페이지 번호, 두 번째 파라미터로는 한 번에 가지고 올 데이터 수를 넣어줌.
           URL 경로에 페이지 번호가 있으면 해당 페이지를 조회하도록 세팅하고, 페이지 번호가 없으면 0페이지를 조회하도록 함.*/
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 3);
        /* 조회 조건과 페이징 정보를 파라미터로 넘겨서 Page<Item> 객체를 반환 받음. */
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);
        /* 조회한 상품 데이터 및 페이징 정보를 뷰에 전달 */
        model.addAttribute("items", items);
        /* 페이지 전환 시 기존 검색 조건을 유지한 채 이동할 수 있도록 뷰에 전달 */
        model.addAttribute("itemSearchDto", itemSearchDto);
        /* 상품 관리 메뉴 하단에 보여줄 페이지 번호의 최대 개수임 */
        model.addAttribute("maxPage", 5);
        return "item/itemMng";

    }


    /* 상품 상세 페이지 */
    @GetMapping("/item/{itemId}")
    public String itemDtl(Model model, @PathVariable("itemId") Long itemId) {
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        model.addAttribute("item", itemFormDto);
        return "item/itemDtl";
    }

}
