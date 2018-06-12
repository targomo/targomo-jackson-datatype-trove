# jackson-datatype-trove
This module facilitates serialization and deserialization of certain Trove objects. So far it supports:
* TIntIntMap
* TIntObjectMap
* TObjectIntMap

This is a forked project from https://bitbucket.org/marshallpierce/jackson-datatype-trove.
It has been slightly amended to work with our jackson version and we added a few serializers/deserializers. 
In the future we will potentially add more of them.

To include add to maven: 
```
<dependency>
    <groupId>com.targomo</groupId>
    <artifactId>jackson-datatype-trove</artifactId>
    <version>0.0.2</version>
</dependency>
```
To include to your Jackson Object Mapper execute:
```
ObjectMapper om = new ObjectMapper();
om.registerModule(new JodaModule()) 
        .registerModule(new TroveModule(-1)); //-1 is the "null" value representative 
```

## Change Log:

### Version 0.0.4
* to fill

### Version 0.0.3
* initial import from private repo

