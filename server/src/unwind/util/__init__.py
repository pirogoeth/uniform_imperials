import random


def generate_random_token(length=64):
    """ Generates a random token of specified length.
    """

    lrange = 16 ** length
    hexval = "%0{}x".format(length)
    return hexval % (random.randrange(lrange))


def token_by_header_data(auth_token):
    """ Accepts the user provided auth token and returns a
        token object if the token is valid, otherwise, None.
    """

    from unwind.models.user import Token

    if not auth_token:
        return None

    try:
        token = Token.get(Token.token == auth_token)
    except Exception:
        return None

    return token
