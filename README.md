# jPdbApi

[![Maven Central
](https://maven-badges.herokuapp.com/maven-central/io.ningyuan/jPdbApi/badge.svg)
](https://maven-badges.herokuapp.com/maven-central/io.ningyuan/jPdbApi)

A Java wrapper for the [RCSB PDB RESTful Web Service](https://www.rcsb.org/pdb/software/rest.do).

## Example

```java
// Search for the keyword 'aquaporin'
Query query = new Query(Query.KEYWORD_QUERY, "aquaporin");
List<String> results = query.execute();

// and get the first result
if (results.size() > 0) {
    Pdb pdb = new Pdb(results.get(0));
    pdb.load();
    System.out.println(pdb.toString());
    // prints "[1FQY] STRUCTURE OF AQUAPORIN-1 AT 3.8 A RESOLUTION BY ELECTRON CRYSTALLOGRAPHY"
}
```
