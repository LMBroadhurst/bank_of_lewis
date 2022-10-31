# Welcome to the Bank of Lewis

To use this app, please download/git clone the repo onto your local PC and open in with your favourite IDE.
It was built using Java 17, so please ensure your JDK is compatible before running the app.

## Key Websites for App use

After entering and running the BolApplication file on port 8080...

### H2 Console: http://localhost:8080/h2-console/
- Easier cross compatibility vs. Postgres, wanted others to view the app as easily as possible
- username/password set as sa/password --> can be changed in application.properties if desired

### SwaggerUI: http://localhost:8080/swagger-ui/index.html#/
- Similar to Postman functionality wise
- Improves UX by displaying the available API calls and templating what variables/bodies are required

## How to use the H2 console

Follow the link provided above, you should then be greeted with the following screen.

![H2 1](https://user-images.githubusercontent.com/95776633/199006917-1b6d1435-36e4-4dcf-9f94-cc3b7f96ffd0.jpg)

Enter the details as shown, the password is password and can be changed in application.properties.

After clicking connect you will be presented with this screen.
From here you can click the + to see the columns of the table & run any SQL statements you desire.

![H2 2](https://user-images.githubusercontent.com/95776633/199006946-0570939f-dcd8-4ac3-887d-169a5b8b32a7.jpg)

If you click directly on ATM you will get an autogenerated 'SELECT * FROM ATM' statement.

In the text box you may enter whatever SQL you like, for example 'SELECT NAME, LOCATION FROM ATM WHERE NOTE50 < 10'.

Here's some SQL to get you started:

```
INSERT INTO ATM (NAME, LOCATION, NOTE20, NOTE50) VALUES ('London1', 'Oxford St', 100, 200);
INSERT INTO ATM (NAME, LOCATION, NOTE20, NOTE50) VALUES ('London2', 'Liverpool St', 10, 5);
INSERT INTO ATM (NAME, LOCATION, NOTE20, NOTE50) VALUES ('London3', 'St Pauls', 5, 10);
INSERT INTO ATM (NAME, LOCATION, NOTE20, NOTE50) VALUES ('London4', 'Mahi Test', 8, 3);
```

## How to use SwaggerUI

The landing page after following the swagger link should show a list of potential API calls, as seen below.

![Swagger 1](https://user-images.githubusercontent.com/95776633/199007316-dbdfe8ac-c4ea-4435-8bb3-7ed4f1a00122.png)

To make a request, click on 'Try it out' in the top right hand corner of the API request, after clicking on the request.

![Swagger 2](https://user-images.githubusercontent.com/95776633/199007655-4bf77a90-08fb-4985-a01e-c5753824a70a.png)

Here we are going to attemp to withdraw $200 from the Mahi Test ATM by filling out the relevant fields and clicking execute.

![Swagger 3](https://user-images.githubusercontent.com/95776633/199008328-d55d8547-28e7-45f2-a328-7b000ee17a5f.png)

We get a 200 reponse code, and a response body stating "5 $20 notes dispensed. 2 $50 notes dispensed."

All other requests follow the same process.

## High level overview of App

Before coding the app, I planned to create a Spring Boot application that interacted with a DB.

After reading the spec required, I wrote an outline for a basic class diagram, seen below.

![pre code](https://user-images.githubusercontent.com/95776633/199010137-11551097-33b8-43ac-9499-58d846383250.png)

## App Features
- ATMs have a supply of bank notes available.
- ATM knows the amount of each note it holds.
- ATM can report how many of each note it has, as well as it's total cash holding.
- ATM can tell it has x $20 and y $50 notes from the POST createAtm method.
- After initialisation, can only add or remove $20 and $50 notes.
- Errors reported when asking for invalid cash withdrawals, e.g. $30.
- Dispensing money removes the equivalent cash from the machine.
- Failure to dispense cash will not reduce the cash in the machine.
- ATM can dispense any valid match of $20s/$50s. As well as e.g. $200 when there are only 3x$50 notes and 8x$20 notes (see Mahi Test, ID 4 on given SQL).
- Requests of e.g. $100, $120 can dispense the maximum number of $20s possible by specifying the prefers20s parameter as true when withdrawing cash.
- The ATM will trigger prefers20s automatically and preferentially dispense $20 if the number of $50s in the ATM is below 5.
- The ATM will print a SOUT message when the number of a specific note in the ATM is below 10. It will also send a notification when the notes hit 0.
- The DB storing the relevant ATM information is persistent. The information will remain in the DB after closing and re-opening the app.
- Notes can be added to the machine number by using the addCash API request.
- Service/Controller/Repo layer testing.

## With more time I would...

### Support all other legal denominations and coinage
I would approach this feature in a very similar way to how I did with just 20s and 50s. There would be a cascade of logic that will try to match the highest value notes first, before trying to match with lower denominations. As using all coins and notes would become a massive piece of logic, I would separate it into a ‘note dispenser’ and ‘coin dispenser’. This would round down the cash required to the closest 5, complete the note dispensing logic, and then complete the coin dispensing logic.

This feature will add complexity in the form of preferring $5/$10 for large sums, at what point would it be too much. The number of different logic components required will make the code susceptible to human error too, lots of testing and clean solutions needed. There will also likely be lots of potential matches of notes.


### Multi-Currency support
Instead of having an ATM that contains the values of note20, note50, etc, as seen in my submitted solution, I would replace them with objects of the relevant currencies. E.g. private Usd usd, private Eur eur. These objects would then contain the note20/note50 values, similar to how I created the cashToAdd object with note50/note20 properties. The dispense logic would change slightly to access the currency object, then the available notes. 

A problem that would occur when working with different currencies is that they do not have equal denominations. E.g. USD has a $1 note, whereas in the UK it would be a £1 coin. Unless you assume all denominations are equal this would add another layer of complexity. 

## What I enjoyed about building this app

- The Pandora's Box of problems and solutions that come from the challenge. When looking at the challenge briefly it seems relatively simple, but when digging into the problem lots of different scenarios arise. Really enjoyed having those moments thinking everything was working, to then test another scenario and find another fire to fight.
- When writing my tests I found that I hadn’t considered what would happen if you entered a negative number, and it ‘dispensed’ -1 note. It was a funny moment for me because when I first started coding I found tests a little tedious as I thought ‘I can see it works, what’s the point’. It was a great full circle moment.
- Solving the problem was one challenge and I had a 80% functional logic sequence but the presentation of the code was bad, making the debugging/coding required to get the last 20% a nightmare. It made me refactor all the code required to dispense notes successfully into a much cleaner format, which was a very rewarding process. It allowed me to make much more sense of the code and completing the functionality was a lot more fun than pre-refactoring.

## What I could improve 

- I haven’t done extensive testing in Spring Boot before and this was a big challenge for me. I think my service layer tests are relatively good but my controller tests definitely need improvement. The problem was I was unsure what to return from my controller methods (ResponseEntity, simple String, etc). I decided on ResponseEntity as it seemed to have good support from sites such as StackOverflow but when I went to test I struggled to find good resources that matched the way I did it. In hindsight I wish I just used the Spring documentation from square 0.
- I’m not sure whether the algorithm I’m using for getting the best match of notes is the most optimal to use. As mentioned, I kept finding extra problems and scenarios that helped me to improve and reiterate more efficient ways to solve problems. I’m sure if I spent more time on it I would have a more efficient way of dispensing notes.
- My code at the beginning didn’t follow SOLID principles correctly and it led to me losing a lot of time confused & refactoring code that should have been done properly from the start. E.g. I would have code checking whether there were enough notes in the ATM and matching the notes together in the same ‘block’. I should have broken the code/problem into much more modular pieces, like the end product (hopefully) is. Key point as it really highlighted how making the code readable for you and the wider team you are in is crucial.

### Any feedback email me at lewis1broadhurst@gmail.com
