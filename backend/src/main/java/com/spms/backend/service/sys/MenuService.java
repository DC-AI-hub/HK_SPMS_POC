package com.spms.backend.service.sys;

import com.spms.backend.service.model.MenuModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MenuService {

    MenuModel createMenu(MenuModel menuModel);

    MenuModel getMenuById(Long id);

    Page<MenuModel> getAllMenus(Pageable pageable);

    MenuModel updateMenu(Long id, MenuModel menuModel);

    void deleteMenu(Long id);

    List<MenuModel> getMenusForUser(Long userId);

    List<MenuModel> getMenusForCurrentUser();

    MenuModel addRoleToMenu(Long menuId, Long roleId);

    MenuModel removeRoleFromMenu(Long menuId, Long roleId);

    boolean menuExistsByTitle(String title);

    boolean menuExistsByPath(String path);

    boolean menuExistsByTitleAndPath(String title, String path);
}
