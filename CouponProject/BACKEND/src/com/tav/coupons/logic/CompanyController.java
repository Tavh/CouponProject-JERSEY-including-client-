package com.tav.coupons.logic;
import com.tav.coupons.beans.*;   
import com.tav.coupons.dao.*;
import com.tav.coupons.enums.ErrorType;
import com.tav.coupons.exceptions.ApplicationException;
import com.tav.coupons.utilities.InputValidationUtils;

import java.util.List;

import javax.servlet.http.Cookie;

public class CompanyController implements IUser {


	private CompanyDao companyDao;


	// ------------------------------------Constructor-------------------------------------

	public CompanyController () {
		this.companyDao = new CompanyDao();
	}

	// ---------------------------------Creates a company------------------------------------

	// Checks if all the details are legitimate and if so, creates a company
	public long createCompany (Company company) throws ApplicationException {

		if (companyDao.isCompanyExists(company.getCompanyName())) {
			throw new ApplicationException(ErrorType.DATA_NOT_FOUND, "There is already a company by the name " + company.getCompanyName() + "!");
		}

		if (!InputValidationUtils.isPasswordValid(company.getPassword())) {
			throw new ApplicationException (ErrorType.ILLEGAL_USER_INPUT, "The password you're trying to use doesn't fit the required format.");
		}

		if (!InputValidationUtils.isEmailValid(company.getEmail())) {
			throw new ApplicationException (ErrorType.ILLEGAL_USER_INPUT, "The email you're trying to use doesn't fit the required format.");
		}

		long autoGeneratedId = companyDao.createCompany(company);

		if (autoGeneratedId > 0) {
			System.out.println("A new company by the name " + company.getCompanyName() + " was created successfully!");
		}

		return autoGeneratedId;
	}

	// ------------------------------Remove a company---------------------------------

	// Removes a company by id
	public void removeCompany(long companyId) throws ApplicationException {

		if (!companyDao.isCompanyExists(companyId)) {
			throw new ApplicationException(ErrorType.DATA_NOT_FOUND, "The company you're trying to remove couldn't be found.");
		}


		companyDao.removeCompany(companyId);
		System.out.println("The company by the id " + companyId + " was removed.");
	}

	// ----------------------Update a company----------------------------------------------

	public void updateCompany (UserDetails userDetails, Cookie[] cookies) throws ApplicationException {

		// This will store the currently active user in the application
		String currentActiveUsername = "None";

		// This for each loop checks if there actually is a cookie for a logged-in company and if so, checks if it fits the one being updated
		for (Cookie c : cookies) {
			if (c.getName().equals("companyName")) {
				currentActiveUsername = c.getValue();
			}
		}

		if (currentActiveUsername.equals(userDetails.getUsername())) {
			
			Company company = new Company(userDetails.getId(), userDetails.getUsername(), userDetails.getPassword(), userDetails.getEmail());

			if (!companyDao.isCompanyExists(company.getId())) {
				throw new ApplicationException(ErrorType.DATA_NOT_FOUND, "The company you're trying to update doesn't exist.");
			}

			companyDao.updateCompany(company);
			System.out.println("Company " + company.getCompanyName() + " was updated successfully!");
		} else {
			// If the logged-in user is different than the one they're trying to update, there is a strong chance they are trying to hack the application
			throw new ApplicationException (ErrorType.HACKING_ATTEMPT, "The company that is logged-in is not the same as the one requested for update");
		}
	}


	// ---------------------------------------Getters-------------------------------------

	// Gets a specific company by id 
	public Company getCompany (long companyId) throws ApplicationException {

		Company company = companyDao.getCompany(companyId);

		/*if (company == null) {
			throw new ApplicationException(ErrorType.DATA_NOT_FOUND, "The company you're trying to get couldn't be found.");
		}*/

		return company;
	}

	// Gets a specific company by name
	public Company getCompany (String companyName) throws ApplicationException {

		Company company = companyDao.getCompany(companyName);

		/*if (company == null) {
			throw new ApplicationException(ErrorType.DATA_NOT_FOUND, "The company you're trying to get couldn't be found.");
		}*/

		return company;
	}

	// Gets all the companies that exist in the database
	public List<Company> getAllCompanies () throws ApplicationException {

		List <Company> allCompanies = companyDao.getAllCompanies();

		if (allCompanies == null) {
			throw new ApplicationException (ErrorType.DATA_NOT_FOUND, "No companies could be found.");
		}

		System.out.println("These are all the companies we could find : " + allCompanies);
		return allCompanies;

	}

	// --------------------------------Checks if user details are valid----------------------------

	@Override
	public boolean authenticate (String companyName, String password) throws ApplicationException {

		if (!companyDao.isCompanyExists(companyName)) {
			throw new ApplicationException(ErrorType.INVALID_USER, "The company you're trying to access couldn't be found.");
		}

		if (!companyDao.getCompany(companyName).getPassword().equals(password)) {
			throw new ApplicationException(ErrorType.INVALID_USER, "Seems like the password is wrong.");
		}

		return true;

	}

	// -------------------------------------------------------------------------------------------
}
