# Simple HTTP Requests (Java)
A small set of java classes for simple asynchronous http requests.  

- Very easy to use
- For fetching text content like html code of online websites
- Customizable request methods (e.g. GET, POST), headers and body content

##Fetching content from google.com (blocking)
```Java
String data = HttpRequest.getData("https://www.google.com/").get().getContent().get();
```