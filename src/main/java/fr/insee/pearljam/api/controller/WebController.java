package fr.insee.pearljam.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Redirect the user to swagger-ui.html
 * @author scorcaud
 *
 */
@Controller
public class WebController {
	 @GetMapping(value = "/")
	   public String redirect() {
	      return "redirect:/swagger-ui.html";
	   }
}
