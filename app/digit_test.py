from PIL import Image
from pyocr import tesseract


def test_text(image_file, lang='eng'):
    return tesseract.image_to_string(
            Image.open(image_file),
            lang=lang,
            builder=tesseract.DigitBuilder())


print test_text('./data/test-japanese.jpg')


