package fr.ymanvieu.trading.webapp.admin.controller;

import java.io.IOException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.ymanvieu.trading.common.admin.AdminService;
import fr.ymanvieu.trading.common.admin.PairInfo;
import fr.ymanvieu.trading.common.admin.SearchResult;
import fr.ymanvieu.trading.common.provider.UpdatedPair;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole(T(fr.ymanvieu.trading.common.user.Role).ADMIN)")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@GetMapping
	public SearchResult symbols(String code) throws IOException {
		return adminService.search(code);
	}

	@PostMapping("/{provider}/{code}")
	public PairInfo add(@PathVariable String code, @PathVariable String provider) throws IOException {
		return adminService.add(code, provider);
	}

	@DeleteMapping("/{pairId}")
	public void delete(@PathVariable Integer pairId, @RequestParam(defaultValue = "false") boolean withSymbol) {
		adminService.delete(pairId, withSymbol);
	}

	@PutMapping
	public PairInfo update(Principal principal, @RequestBody UpdatedPair pair) throws IOException {
		return adminService.update(pair, Long.valueOf(principal.getName()));
	}
}
