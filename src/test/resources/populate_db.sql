-- Insert data into bots_data table
INSERT INTO bots_data (id, bitmex_key, bitmex_secret, user_name, user_email, user_account, user_currency, step, level,
                       coefficient, last_buy, last_sell, strategy)
VALUES (1, 'key1', 'secret1', 'User1', 'user1@example.com', 12345, 'USD', 0.01, 5, 1.0, 100.0, 50.0,
        'LINEAR_BUY_AND_SELL')
     , (2, 'key2', 'secret2', 'User2', 'user2@example.com', 67890, 'EUR', 0.02, 3, 0.5, 200.0, 75.0,
        'LINEAR_BUY_AND_SELL');

-- Insert data into bots table
INSERT INTO bots (bot_id, bot_data_entity_id)
VALUES (1, 1)
     , (2, 2);

-- Insert data into orders table
INSERT INTO orders (filled_price, ord_status, ord_type, order_id, order_qty, price, side, symbol, bot_id)
VALUES (150.0, 'FILLED', 'LIMIT', 'order123', 1.0, 155.0, 'BUY', 'BTC/USD', 1)
     , (200.0, 'FILLED', 'LIMIT', 'order456', 2.0, 198.0, 'SELL', 'ETH/USD', 2);
