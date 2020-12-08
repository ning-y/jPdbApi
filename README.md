# jPdbApi

[![Maven Central
](https://maven-badges.herokuapp.com/maven-central/io.ningyuan/jPdbApi/badge.svg)
](https://maven-badges.herokuapp.com/maven-central/io.ningyuan/jPdbApi)

A Java wrapper for the [RCSB PDB RESTful Web Service](https://www.rcsb.org/pdb/software/rest.do).
I wrote this module specifically for the Android application [*Palantir*](https://github.com/ning-y/palantir), so I've only implemented what was necessary.
Contributions are extremely welcome!

## Example

```java
try {
    TextSearch textSearch = new TextSearch(queryString);
    String[] results = textSearch.getPage();  // PDB IDs
    for (String pdbId : results) {
        Pdb pdb = new Pdb(pdbId);
        pdb.load()
        System.out.println(pdbId.getTitle());
    }
} catch (IOException e) {
    e.printStackTrace();
}
```

## Development guide

### Testing

`mvn test` runs the test.
To run a single test, use the `-Dtest=ClassName#methodName` flag, e.g.

```
mvn test -Dtest=TextSearchTests#nextPageWorksOnline
```

In the event that several tests seem to fail with a `SocketTimeoutException`, check if the RCSB search service is online by running [this simple query](http://search.rcsb.org/rcsbsearch/v1/query?json=%7B%22query%22:%7B%22type%22:%22terminal%22,%22service%22:%22text%22,%22parameters%22:%7B%22attribute%22:%22exptl.method%22,%22operator%22:%22exact_match%22,%22value%22:%22ELECTRON%20MICROSCOPY%22%7D%7D,%22return_type%22:%22entry%22%7D) listed on the RCSB ["Search API Documentation"](http://search.rcsb.org/#basic-queries) page.

### Illegal reflective access operation from guice

You might see the warning:

```
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by com.google.inject.internal.cglib.core.$ReflectUtils$1 (file:/usr/share/maven/lib/guice.jar) to method java.lang.ClassLoader.defineClass(java.lang.String,byte[],int,int,java.security.ProtectionDomain)
WARNING: Please consider reporting this to the maintainers of com.google.inject.internal.cglib.core.$ReflectUtils$1
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
```

when building or testing the package.
This is a [documented bug](https://github.com/google/guice/issues/1133).
I expect (hope) that [the fix](https://github.com/google/guice/issues/1133#issuecomment-656906686) will propagate soon (guice is not a direct dependency).
