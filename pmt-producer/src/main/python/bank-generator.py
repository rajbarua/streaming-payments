import random
import string

def generate_bank_symbols(num_symbols):
    symbols = set()
    while len(symbols) < num_symbols:
        symbol = ''.join(random.choices(string.ascii_uppercase, k=3))
        symbols.add(symbol)
    return symbols

def write_bank_symbols_to_file(file_path, num_symbols):
    symbols = generate_bank_symbols(num_symbols)
    with open(file_path, 'w') as file:
        for symbol in symbols:
            file.write(symbol + '\n')

write_bank_symbols_to_file('/Users/raj/src/streaming-payments/pmt-producer/src/main/resources/banks.txt', 100)

