package com.umc5th.muffler.domain.category.controller;

import static com.umc5th.muffler.global.response.code.ErrorCode.*;

import com.umc5th.muffler.domain.category.dto.GetCategoryListResponse;
import com.umc5th.muffler.domain.category.dto.NewCategoryResponse;
import com.umc5th.muffler.domain.category.dto.DeleteCategoryResponse;
import com.umc5th.muffler.domain.category.dto.NewCategoryRequest;
import com.umc5th.muffler.domain.category.dto.UpdateCategoryNameIconRequest;
import com.umc5th.muffler.domain.category.dto.CategoryFilterUpdateRequest;
import com.umc5th.muffler.domain.category.service.CategoryService;
import com.umc5th.muffler.global.response.Response;
import com.umc5th.muffler.global.swagger.ErrorResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
@Validated
@Tag(name = "Category", description = "카테고리 API")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "카테고리 생성 API",
            description = "새 카테고리를 저장한다. 가지고 있는 카테고리 중에서 중복된 이름이 있으면 등록에 실패한다. "
                    + "생성된 카테고리의 우선순위는 제일 나중에 나오도록 설정된다.")
    public Response<NewCategoryResponse> createCategory(Principal principal, @RequestBody @Valid NewCategoryRequest request) {
        NewCategoryResponse newCategory = categoryService.createNewCategory(principal.getName(), request);
        return Response.success(newCategory);
    }

    @PatchMapping
    @Operation(summary = "카테고리 이름, 아이콘 변경 API",
            description = "특정 카테고리의 아이콘이나 이름을 업데이트 한다. 가지고 있는 카테고리 중 중복되는 이름은 허용되지 않고, "
                    + "기본으로 가지고 있는 카테고리의 아이콘은 변경할 수 없다. 카테고리 아이콘/이름 수정 화면에서 저장버튼 누를 시 호출")
    @ErrorResponses(value = {
            MEMBER_NOT_FOUND, CATEGORY_NOT_FOUND, CANNOT_UPDATE_ETC_CATEGORY_NAME, DUPLICATED_CATEGORY_NAME, CANNOT_UPDATE_DEFAULT_ICON
    })
    public Response<Void> updateCategoryNameOrIcon(Principal principal, @RequestBody @Valid UpdateCategoryNameIconRequest request) {
        categoryService.updateNameOrIcon(principal.getName(), request);
        return Response.success();
    }

    @PatchMapping("/filter")
    @Operation(summary ="카테고리 숨김 여부,순서 변경 API",
            description = "유저가 가진 카테고리들의 보여줄 순서와 숨김여부를 변경한다. 순서는 1부터 시작하는 연속된 숫자로 각 카테고리별로 부여되어야 한다. " 
                    + "전체 카테고리 (숨김 카테고리 포함)를 보는 화면에서 뒤로가기를 누를 때 호출")
    @ErrorResponses(value = {
        MEMBER_NOT_FOUND, CATEGORY_UNEXPECTED_ORDER, CATEGORY_BATCH_INSERT_FAIL
    })
    public Response<Void> updateCategoryPriorityVisibility(
            Principal principal,
            @RequestBody @Valid CategoryFilterUpdateRequest request) {
        categoryService.updateBatchPriorityOrVisibility(principal.getName(), request);
        return Response.success();
    }

    @DeleteMapping("/{categoryId}")
    @Operation(summary = "카테고리 삭제 API", description = "카테고리를 삭제하는 API, 반환되는 값은 이 때 \"기타\" 카테고리로 자동 분류되는 "
            + "삭제되는 카테고리로 등록해둔 반복기록의 개수이다. 이미 삭제된 카테고리와 기본 카테고리는 삭제할 수 없다.")
    @ErrorResponses(value = {MEMBER_NOT_FOUND, CATEGORY_NOT_FOUND, ALREADY_INACTIVE_CATEGORY, CANNOT_DELETE_DEFAULT_CATEGORY})
    public Response<DeleteCategoryResponse> deleteCategory(Principal principal, @PathVariable("categoryId") Long categoryId) {
        System.out.println(principal.getName());
        DeleteCategoryResponse response = categoryService.deactivateCategory(principal.getName(), categoryId);
        return Response.success(response);
    }

    @GetMapping
    @Operation(summary = "숨긴 카테고리 포함 전체 카테고리 조회 API", description = "숨긴 카테고리를 포함한 전체 카테고리를 조회하는 API, "
            + "priority를 오름차순으로 정렬하여 가지고 있는 모든 카테고리를 리스트 형식으로 반환한다. 카테고리 편집화면에 진입할 때 사용")
    @ErrorResponses(value = {MEMBER_NOT_FOUND})
    public Response<GetCategoryListResponse> getCategoryList(Principal principal) {
        GetCategoryListResponse response = this.categoryService.getActiveCategories(principal.getName());
        return Response.success(response);
    }
    
    @GetMapping("/filter")
    @Operation(summary = "숨김 처리 하지 않은 카테고리의 이름, 아이콘 조회 API",
            description = "카테고리 선택 뷰에 필요한 카테고리 아이콘, 이름, 아이디 조회 API, "
                    + "우선순위 별로 정렬하여 제공한다. 홈 화면, 소비 등록, 목표 등록, 반복 기록 등록에서 사용")
    @ErrorResponses(value = {MEMBER_NOT_FOUND})
    public Response<GetCategoryListResponse> getVisibleOutlineCategoryList(Principal principal) {
        GetCategoryListResponse response = this.categoryService.getVisibleOutlineCategories(principal.getName());
        return Response.success(response);
    }
}
