# Report

## Exercise 1 - Transaction, Renting a DVD

The approach we have chosen to handle concurrency concerns takes advantage of the fact that the management of the rentals is restricted to this service. As only this method implements the rental of an item, we can lock the inventory row, allowing reads and writes only when the method is done. As this is a database lock, it means that any instance of the application accessing the same database would be affected by this lock.

This contrasts with the java's locks, which could either be scoped per service instance or per application instance, depending on the implementation.
It means that any other instance of the application would still raise concurrency concerns. We could also mention that the lock, again depending on the implementation, could only allow one rental operation at a time, which is not optimal as the number of concurrent users grows.

This is not an issue with our current solution, as the lock is done per inventory item.

Another approach to handle the concurrency concerns would be to lock the reads on the last rental of the inventory item, which would prevent the method to execute concurrently per item in the same fashion as currently implemented. Although it allows the deletion of the inventory item at the same time the rental is processed.

## Exercise 3 - Implement authentication for staff

**Report** Explain why the password storage in Sakila `Staff` table is insecure. Provide a proposal for a more secure password storage.

Problems :
- Usage of SHA-1, which is outdated and vulnerable to attacks (collisions, brute-force with rainbow tables).
- Lack of a unique salt, increasing risks in case of a data breach.

Proposals for more secure password storage :
- Modern Algorithm: Use bcrypt, Argon2, or PBKDF2 to slow down brute-force attacks.
- Add Unique Salt: Generate a random salt for each user and store it in the database.

**Report** Describe the flow of HTTP requests of the above test case and explain:
* What is sent at step 1 to authenticate to the application and how it is transmitted. 
  * Fields j_username and j_password sent via POST to /j_security_check.
* What is the content of the response at step 2 that his specific to the authentication flow.
  * If success, redirection to a protected page (HTTP 302), redirection to error.html otherwise.
* What is sent at step 3 to authenticate the user to the application and how it is transmitted.
  * Upon authentication, a JSESSIONID cookie is sent to identify the user in subsequent requests

Explain why the above test authentication flow is considered insecure, if it was used in a productive environment as is, and what is required to make it secure.

- No HTTPS (sensitive data can be intercepted), sha-1 (inadequate for password security) and weak password.
- Enable HTTPS, enforce minimum length, special character, etc for password, use secure cookies, add an additional security layer (MFA) and replace sha-1 with modern hashing algorithm.

## Exercise 5 - Implement a frontend for rentals

The approach brought by HTMX allows us to remove a lot of the boilerplate related to dynamic data fetching, and relocates the logic
to be directly with the content in the HTML code, instead of separated in the JS file, all this without a bundler.

Returning HTML instead of returning another format, and converting it in the frontend make it for an easier experience,
because you don't have to add another language to know.


The reduction of boilerplate is a huge plus, but it requires quite a lot of perspective changes on how we need to work for some features.
It took quite a bit of trial and error before getting the "Unselect" button to work for example, because the `from:`
in the `hx-trigger` attribute is evaluated on mount, so the select button that appears afterwards simply does not work.
I had to listen to the body and filter the events for my specific DOM id to make it work correctly.

All in all, it seems like a good experience, with quite a bit of papercuts because you are doing something different
from the traditional react / js framework system.