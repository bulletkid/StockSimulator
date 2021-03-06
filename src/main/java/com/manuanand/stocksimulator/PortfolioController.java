package com.manuanand.stocksimulator;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller	// This means that this class is a Controller
@RequestMapping(path="/portfolio") // This means URL's start with /portfolio (after Application path)
public class PortfolioController {
	@Autowired // This means to get the bean called portfolioRepository
			   // Which is auto-generated by Spring, we will use it to handle the data
	private PortfolioRepository portfolioRepository;

	@GetMapping(path="/")
	public @ResponseBody Iterable<Portfolio> getAllPortfolios() {
		
		// This returns a JSON or XML with the portfolios
		return portfolioRepository.findAll();
	}

	@GetMapping(path="/{id}")
	public @ResponseBody Portfolio getSpecificPortfolio(@PathVariable String id) {
		
		Integer portfolioId = null;
		try {
			portfolioId = Integer.parseInt(id);
		} catch (NumberFormatException ex) {
			return null;
		}

		Optional<Portfolio> portfolio = portfolioRepository.findById(portfolioId);
		if (!portfolio.isEmpty()) {
			return portfolio.get();
		} 

		return null;
	}
}