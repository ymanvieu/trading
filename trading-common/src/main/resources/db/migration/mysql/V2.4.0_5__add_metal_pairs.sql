insert into pair (symbol,name,source,target,exchange,provider_code) 
(select concat(code,currency,'=X'),name,code,currency,'CCY','YAHOO' from symbols where code like 'X%' and code !='XOM');