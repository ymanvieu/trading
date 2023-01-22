update assets
set quantity = quantity * 20
where symbol_code in( 'GOOG', 'GOOGL', 'AMZN');
