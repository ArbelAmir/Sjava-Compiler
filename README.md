# sjava-compiler
This is a final project in the Introduction to Object Oriented Programming course. In this exercise we were asked to use a compiler for a language called sjava, a language similar to the java language and invented for the course.


#about the project

this is a final exercise for the intr



File description
=============================

oop.ex6.main.DataMember.java - A class representing a data member in the code.
oop.ex6.main.FileReader.java - A static class that reads the file given.
oop.ex6.main.MainCollector.java - A class that collects the global data members and methods.
oop.ex6.main.Scope.java - A class that represents a scope in the code.
oop.ex6.main.ScopeBuilder.java = A class that builds a scope.
oop.ex6.main.Sjavac.java - A class that serves as the "main" in the code.
oop.ex6.main.PatternPool - A class that contains all the regex in the package.


Design
=============================

While designing the code we understood there is a need to divide it into scopes each containing data members.
So we created the Scope and DataMember classes.

Later we saw importance in splitting Scope from its builder because scope is also needed after it was built
by its sub-scopes. But this is not vice versa so the scope does not know anything about its sub scopes.

We saw big similarity between different types of scopes (if/while, methods, main scope) so we did not create
a different class for each one of them or different builder but united them to a general Scope and a general
ScopeBuilder. Same thing happened with different kinds of data members.

Then we understood there are many special features that the mainScope should have that are different from
other scopes so we created MainCollector. Its goal is to collect the global data members and methods so
other scopes will not be able to change them permanently.

Last, we separated reading the file from the main to make it shorter and clear and so we did with all the
regex patterns.



our way to handle adding classes to sjava:

Given the need to implement class compiling in sjava we can do this by creating a class representing the
class group. Each object from the class has the following fields:
name - representing its name,
collector - A MainCollector object collects the global urinals and posters
Scope - An object representing the outer scope of the class, as we have used in this exercise.
DataMembers - List of class dataMembers.

In addition the collector will contain a list of classes.

In this way, the outer scope holds a collector who holds the various departments that hold a scope containing
sub-scopes and dataMembers.

Another change will be to change the global lists to non-static lists and each sub-collector will receive the
global predecessors, each scope will hold the global through the collector.

One additional role to the colector would be to verify the existence of the constructor to the class
(obviously given that it was not created in such a hypothetical manner).



In order to add new types of variables we would have to create a regex check for this type and add it in two
functions: getInvokeParametersTypes in ScopeBuilder and isValueMatchType in DataMember.

In order to enable other types of methods we would have to check by isValueMatchType in DataMember that the
type of the data member being returned by the method matches the type of the function. We should also check
that if a variable is being assigned this value, its type fits the value.



Error Handling
=============================


I/O errors are being handled as general Exceptions with a message so in the moment they are being caught
they are sent to printIOException in sjavac that gets the message and prints it using err.

About other errors, we planned on doing the same but an email was sent that it is unnecessary so we just gave
up about printing them and stopped creating new messages.



Regex Description
=============================

---------------------------------------------------------------
        (^.*[(]\s*)|(\s*&&\s*)|(\s*\|\|\s*)|\s*[)].*$
---------------------------------------------------------------

this regex contains 4 groups which every one of them is match one of the requirments for spliting the
parameters in a if\while parameter box:

The first group contains all possible characters until the parentheses are opened. The ^ symbol indicates
that it is at the beginning of the line, and * characterizes all characters until they are opened.
Then there are 3 "perception" options:
\\ s * represents Ricky characters and spaces
\ | \ | Represents the logical sign "or"
&& represents the logical sign "and"

And in the last group we catch all the characters starting from closing the brackets and away.

In this way, the regex takes exactly the characters that are not the values themselves.

It is used when the line is divided into different parameters and values.


-------------------------------------------------------------
                (void\\s+)|([(])|([,])|([)].*)
-------------------------------------------------------------

The first group takes the word 'void' and requires at least one space after it to catch. The second group
occupies parentheses, the third one catches all the single commas and the fourth catches the closing
parentheses and all the rest subsequent characters.

In this way we perceive all the characters in the function declaration's line, including the words
that represent the variable type.

This group of characters then undergoes additional processing in the process of producing the variables.
