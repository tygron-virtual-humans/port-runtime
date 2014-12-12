%%%%% Common knowledge of ingredients that are needed for making a coffee product. %%%%%

requiredFor(coffee, water).
requiredFor(coffee, grounds).
requiredFor(espresso, coffee).
requiredFor(grounds, beans).

canMake(grinder, [grounds]).
canMake(maker, [coffee, espresso]).

%%%%% General knowledge about providing products. %%%%%

% A Product is a raw product if there are no other products required for making it.
rawProduct(Product) :- not(requiredFor(Product, _)).

% A Machine can provide a Product if it is on the list of Products it can make or it has the Product.
% We sort the list to make sure a unique list is produced each time.
canProvide(Machine, Products) :- canMake(Machine, ProductList1),
		findall(Product, have(Product), ProductList2), union(ProductList1, ProductList2, ProductList), sort(ProductList, Products).
% A Machine can provide a Product if it is on the list of Products it can provide.
canProvideIt(Machine, Product) :- canProvide(Machine, Products), member(Product, Products).
% A Machine can make a Product itself if it is on the list of Products is can make.
canMakeIt(Machine, Product) :- canMake(Machine, Products), member(Product, Products).

%%%%% Definition of indicative message; used for selecting messages that will be deleted from the mail box. %%%%%

% A message is indicative if it is not interrogative nor imperative
% as it must be one of these three types. 
indicative(Message) :- not(Message = imp(_)), not(Message = int(_)).
