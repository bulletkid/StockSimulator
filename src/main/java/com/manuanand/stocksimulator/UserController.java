package com.manuanand.stocksimulator;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

@Controller	// This means that this class is a Controller
@RequestMapping(path="/user") // This means URL's start with /user (after Application path)
public class UserController {
	@Autowired // This means to get the bean called userRepository
			   // Which is auto-generated by Spring, we will use it to handle the data
	private UserRepository userRepository;
	
	@Autowired
	private StockRepository stockRepository;
	
	@Autowired 
	private TradeRepository tradeRepository;

	@Autowired
	private PortfolioRepository portfolioRepository;

	///
	// User Repository
	///
	@PostMapping(path="/add") // Map ONLY POST Requests
	public @ResponseBody User addUser (
			@RequestParam String name, @RequestParam Double amount,
			@RequestParam String email, @RequestParam String password) {

		User newUser = new User();
		newUser.setName(name);
		newUser.setAmount(amount);
		newUser.setEmail(email);
		newUser.setPassword(password);
		
		userRepository.save(newUser);
		
		return newUser;
	}

	@PostMapping(path="/buy") // Map ONLY POST Requests
	public @ResponseBody Trade buyStocks (@RequestParam Integer userId, @RequestParam String password,
					@RequestParam Integer stockId, @RequestParam Integer quantity) {
		
		// Validate stock ID
		Optional<Stock> stock = stockRepository.findById(stockId);
		if (stock.isEmpty()) {
			throw new ResponseStatusException(
					  HttpStatus.NOT_FOUND, "Stock with ID" + stockId + " not found");
		}
		
		// Validate user ID
		Optional<User> user = userRepository.findById(userId);
		if (user.isEmpty()) {
			throw new ResponseStatusException(
					  HttpStatus.NOT_FOUND, "User with ID" + userId + " not found");
		}
		
		if ( (user.get().getPassword() == null) || !user.get().getPassword().equalsIgnoreCase(password)) {
			throw new ResponseStatusException(
					  HttpStatus.BAD_REQUEST, "Password mismatched for User: " + userId);
		}
		
		// Does user has enough money
		if (stock.get().getPrice() * quantity > user.get().getAmount()) {
			throw new ResponseStatusException(
					  HttpStatus.BAD_REQUEST, "User: " + userId + " doesn't have enough credits.");
			
		}
		
		// Everything checks out - Create trade
		Trade trade = new Trade();
		trade.setUserId(userId);
		trade.setStockId(stockId);
		trade.setPricePerStock(stock.get().getPrice());
		trade.setNumStocks(quantity);
		trade.setTradeDate(new Date());
		trade.setType("BUY");
		
		tradeRepository.save(trade);
		
		// Update user's amount
		User userEntry = user.get();
		userEntry.setAmount(userEntry.getAmount() - (stock.get().getPrice() * quantity) );
		userRepository.save(userEntry);

		// Create a portfolio entry
		Portfolio portfolio = new Portfolio();
		portfolio.setUserId(userId);
		portfolio.setStockId(stockId);
		portfolio.setPricePerStock(stock.get().getPrice());
		portfolio.setNumStocks(quantity);
		
		portfolioRepository.save(portfolio);
		
		// Return
		return trade;
	}

	@PostMapping(path="/sell") // Map ONLY POST Requests
	public @ResponseBody Trade sellStocks (@RequestParam Integer userId, @RequestParam String password,
			@RequestParam Integer portfolioId, @RequestParam Integer quantity) {
		
		// Validate user ID
		Optional<User> user = userRepository.findById(userId);
		if (user.isEmpty()) {
			throw new ResponseStatusException(
					  HttpStatus.NOT_FOUND, "User with ID" + userId + " not found");
		}
		
		if (!user.get().getPassword().equalsIgnoreCase(password)) {
			throw new ResponseStatusException(
					  HttpStatus.BAD_REQUEST, "Password mismatched for User: " + userId);
		}

		// Validate portfolio ID
		Optional<Portfolio> portfolio = portfolioRepository.findById(portfolioId);
		if (portfolio.isEmpty()) {
			throw new ResponseStatusException(
					  HttpStatus.NOT_FOUND, "Portfolio with ID" + portfolioId + " not found");
		}
		
		if (portfolio.get().getNumStocks() < quantity) {
			throw new ResponseStatusException(
					  HttpStatus.BAD_REQUEST, "Number of stocks in portfolio are lower than quantity to be sold.");
		}
		
		// Get Stock
		Optional<Stock> stock = stockRepository.findById(portfolio.get().getStockId());
		if (stock.isEmpty()) {
			throw new ResponseStatusException(
					  HttpStatus.NOT_FOUND, "Stock with ID" + portfolio.get().getStockId() + " not found");
		}
		
		// Everything checks out - Create trade
		Trade trade = new Trade();
		trade.setUserId(userId);
		trade.setStockId(portfolio.get().getStockId());
		trade.setPricePerStock(stock.get().getPrice());
		trade.setNumStocks(quantity);
		trade.setTradeDate(new Date());
		trade.setType("SELL");
		
		tradeRepository.save(trade);
		
		// Update user's amount
		Double amountGained = quantity * stock.get().getPrice();
		User userEntry = user.get();
		userEntry.setAmount(userEntry.getAmount() + amountGained);
		userRepository.save(userEntry);

		// Update portfolio
		portfolio.get().setNumStocks(portfolio.get().getNumStocks() - quantity);
		portfolioRepository.save(portfolio.get());
		
		// Return
		return trade;
	}

	@GetMapping(path="/")
	public @ResponseBody Iterable<User> getAllUsers() {
		
		// This returns a JSON or XML with the users
		return userRepository.findAll();
	}

	@GetMapping(path="/{id}")
	public @ResponseBody User getSpecificUser(@PathVariable String id) {
		
		Integer userId = null;
		try {
			userId = Integer.parseInt(id);
		} catch (NumberFormatException ex) {
			return null;
		}

		Optional<User> user = userRepository.findById(userId);
		if (!user.isEmpty()) {
			return user.get();
		} 

		return null;
	}
}