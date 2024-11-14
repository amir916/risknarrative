# Company Search Application

## Testing 
<ul>
  <li>The Testing Pyramid approach has been followed.</li>
  <li>TDD for unit testing. The test coverage is almost 100%.</li>
  <li>There is a API level test in order to test for testing the request/response payload.</li>
  <li>One component test is written to cover E2E with Wiremock for subbing the outbound calls</li>
  <li>Couldn't do an integration test for the outbound api calls with RestTemplate client due to lack of time but 
    it the client has been thoroughly unit tested.</li>
</ul>

## Potential Improvements
<ul>
  <li>Swagger API for the endpoint documentation</li>
  <li>Use of some library (e.g., ModelMapper) to map the DTOs.</li>
  <li>Value annotations for properties in TruProxyClientTest.java. Couldn't avail time to complete it.</li>
  <li>Used in-memory map to improvise the DB. This could be replaced with some proper DB.</li>
</ul>

