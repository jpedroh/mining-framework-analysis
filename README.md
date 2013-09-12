entity-resolution
===================

This project is an interactive entity resolution plugin for [Elasticsearch](http://www.elasticsearch.org) based on [Duke](http://code.google.com/p/duke). Basically, it uses [Bayesian probabilities] (http://en.wikipedia.org/wiki/Bayesian_probability) to compute probability. You can pretty much use it an interactive deduplication engine.

It is usable as is, though ```cleaners``` are not yet implemented.

To understand basics, go to [Duke project documentation](http://code.google.com/p/duke/wiki/XMLConfig).

A list of [available comparators] (http://code.google.com/p/duke/wiki/Comparator) is available [here](http://code.google.com/p/duke/wiki/Comparator).

## Install

``` 
$ plugin -i entity-resolution -url 	http://goo.gl/LMF3wK
```

## Request
 ```javascript
{
  "query": {
    "custom_score": {
      "query": {
        "match": {
          "name": "foo"
        }
      },
      "script": "entity-resolution",
      "lang": "native",
      "params": {
        "entity": [
            {
                "field" : "name",
                "value" : "Arthur",
                "cleaners" : ["asciifolding","lowercase"],
                "comparator" : "no.priv.garshol.duke.comparators.PersonNameComparator",
                "low" : 0.5,
                "high" : 0.95
            },
            {
                "field" : "surname",
                "value" : "Raimbault",
                "cleaners" : ["asciifolding"],
                "comparator" : "no.priv.garshol.duke.comparators.Levenshtein",
                "low" : 0.5,
                "high" : 0.95
            }            
        ]
      }
    }
  }
}
```

* ```entity``` should always be an array.
* ```field``` is the name of the field to compare to.
* ```value``` is the value of the field to compare.
* ```cleaners``` is the list of data cleaners to apply (not implemented yet). Should always be an array.
* ```comparator``` is the full qualified class name of the comparator to use. Note : you can implement your own, and put it in the claspath. It should work (not tested yet).
* ```low``` is the lowest probability for this field (if the probability is inferior, this one will be used).
* ```high``` is the highest probability for this field (if the probability is superior, this one will be used).

## Licence 

This project is licended under LGPLv3

Copyright (c) 2013 Yann Barraud