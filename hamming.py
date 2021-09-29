from random import randint


def encode_word(four_word):
    seven_word = four_word;

    seven_word <<= 1
    seven_word += ((four_word >> 3) ^ (four_word >> 2) ^ (four_word >> 1)) & 1
    seven_word <<= 1
    seven_word += ((four_word >> 2) ^ (four_word >> 1) ^ four_word) & 1
    seven_word <<= 1
    seven_word += ((four_word >> 3) ^ (four_word >> 2) ^ four_word) & 1
    
    return seven_word


def encode(four_word_message):
    return list(map(encode_word, four_word_message))


def fix_error(seven_word):
    s1 = ((seven_word >> 6) ^ (seven_word >> 5) ^ (seven_word >> 4) ^ (seven_word >> 2)) & 1
    s2 = ((seven_word >> 5) ^ (seven_word >> 4) ^ (seven_word >> 3) ^ (seven_word >> 1)) & 1
    s3 = ((seven_word >> 6) ^ (seven_word >> 5) ^ (seven_word >> 3) ^ seven_word) & 1
    syndrome = (s1, s2, s3)
    
    if syndrome == (0, 0, 0):
        ...
    elif syndrome == (1, 0, 1):
        seven_word ^= 0b1000000
    elif syndrome == (1, 1, 1):
        seven_word ^= 0b0100000
    elif syndrome == (1, 1, 0):
        seven_word ^= 0b0010000
    elif syndrome == (0, 1, 1):
        seven_word ^= 0b0001000
    elif syndrome == (1, 0, 0):
        seven_word ^= 0b0000100
    elif syndrome == (0, 1, 0):
        seven_word ^= 0b0000010
    elif syndrome == (0, 0, 1):
        seven_word ^= 0b0000001
    
    return seven_word


def decode(seven_word_message):
    four_word_message = []
    
    for seven_word in seven_word_message:
        seven_word = fix_error(seven_word)
        four_word_message.append(seven_word >> 3)
    
    return four_word_message


def add_noise(seven_word_message):
    return list(map(lambda x: x ^ (1 << randint(0, 6)), seven_word_message))
        

if __name__ == '__main__':
    print('Enter your message')
    message = input()
    
    bin_message = message.encode()
    four_word_message = []
    for byte in bin_message:
        four_word_message.append(byte >> 4)
        four_word_message.append(byte & 0b00001111)
    
    print('\n4-bit words')
    for four_word in four_word_message:
        print(bin(four_word)[2::].zfill(4), end=' ')
    
    seven_word_message = encode(four_word_message)
    
    print('\n\n7-bit words (encoded message)')
    for seven_word in seven_word_message:
        print(bin(seven_word)[2::].zfill(7), end=' ')
    
    seven_word_message = add_noise(seven_word_message)
    
    print('\n\n7-bit words (noisy message)')
    for seven_word in seven_word_message:
        print(bin(seven_word)[2::].zfill(7), end=' ')
    
    four_word_message = decode(seven_word_message)
    
    print('\n\n4-bit words (decoded)')
    for four_word in four_word_message:
        print(bin(four_word)[2::].zfill(4), end=' ')
    
    bin_message = []
    even = True
    for four_word in four_word_message:
        if even:
            half_byte = four_word << 4
        else:
            bin_message.append(half_byte + four_word)
        even = not even
    
    message = bytearray(bin_message).decode()
    
    print('\n\nDecoded message')
    print(message)
