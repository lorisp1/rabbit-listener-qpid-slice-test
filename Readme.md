This is a simple PoC showing how a `@RabbitListener` can be tested using an embedded [Qpid](https://qpid.apache.org/) 
instance (useful for instance when you cannot use Test Containers) and a sliced Spring Application Context. That is, 
application's `pom.xml` declares a MongoDB dependency but the corresponding autoconfiguration will not be activated during
the test execution, so the test will only load the necessary portion of Spring Application Context, similarly to what you
would do with `@WebMvcTest` or `@DataJpaTest`.

To run the test, execute the following command:

```mvn clean test```