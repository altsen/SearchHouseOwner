import re
import requests
import bs4
import codecs
import sqlite3
import leancloud
from leancloud import Object
from leancloud import Query


leancloud.init('aIvRjO9nNktIWXYTORzbIoQS-gzGzoHsz', 'XedakwWQtCVo5ADjvdHm3gw6')

class HouseInfo(leancloud.Object):
    def __init__(self, **kwargs):
        super(HouseInfo, self).__init__()
        for k, v in kwargs.items():
            self.set(k, v)
query = Query(HouseInfo)

def ganji_get_house_list(district):
    result = []
    for i in range(1, 200):
        r = requests.get("http://bj.ganji.com/fang1/"+district+"/o%d"%i)
        result = re.findall("/fang./[^.]*\.htm", r.text)
        for s in result:
            yield "http://bj.ganji.com"+s

def ganji_get_house_page(url):
    try:
        result = dict()
        result["source"] = url
        result["site"] = "ganji"
        r = requests.get(url)
        soup = bs4.BeautifulSoup(r.text, "html.parser")
        result["title"] = soup.find("h1", "title-name").text
        result["create_at"] = re.sub("[^-0-9: ]*", "", soup.find("ul", "title-info-l").text)
        basic = soup.find("div", class_="basic-box")
        result["img"] = basic.find("div", "basic-imgs-big").find("img")['src']
        ul = basic.find("ul", "basic-info-ul")
        lis = ul.find_all("li")
        result["price"] = lis[0].find("b", "basic-info-price").text
        result["huxing"] = lis[1].text.strip()
        result["gaikuang"] = lis[2].text.strip()
        result["louceng"] = lis[3].text.strip()
        result["xiaoqu"] = lis[4].find('a')["title"]
        result["weizhi"] = "-".join(map(lambda x: x.text, lis[5].find_all('a')))
        result["peizhi"] = lis[7].text.strip()

        contact = basic.find("div", "basic-info-contact")
        result["owner"] = basic.find("div", "contact-person").text.strip()
        result["phone"] = basic.find("em", "contact-mobile").text.strip()

        for k, v in result.items():
            if k == "create_at": continue
            v = v.replace(" ", "")
            v = v.replace("\n", "")
            result[k] = v
        return result

    except:
        return None

def wuba_get_house_list(district):
    result = []
    for i in range(1, 2):
        r = requests.get("http://bj.58.com/"+district+"/chuzu/pn%d"%i)
        result = re.findall("http://jump.zhineng.58.com/clk[^\"]*", r.text)
        for s in result:
            yield s

def wuba_get_house_page(url):
    result = dict()
    result["source"] = url
    result["site"] = "wuba"
    r = requests.get(url)
    soup = bs4.BeautifulSoup(r.text, "html.parser")
    result["title"] = soup.find("h1", "main-title").text
    result["create_at"] = re.sub("[^-0-9: ]*", "", soup.find("div", "title-right-info").find("span").text)
    basic = soup.find("ul", "house-primary-content")
    result["img"] = soup.find("img", id="smainPic")["src"]
    lis = basic.find_all("li", "house-primary-content-li")
    result["price"] = lis[0].find("em", "house-price").text
    result["huxing"] = lis[1].text
    
    return result

def save_to_leancloud(d):
    h = HouseInfo(**d)
    h.save()

def run():
    pages = ganji_get_house_list("chaoyang")
    for p in pages:
        print p
        query.equal_to('source', p)
        if not query.find():
            r = ganji_get_house_page(p)
            if r: save_to_leancloud(r)

def test():
    with codecs.open("output.txt", "w", "utf-8") as f:
        r = wuba_get_house_page("http://jump.zhineng.58.com/clk?target=mv7V0A-b5HThmvqfpv--5yndsvOJRh7_r7IYEbOCyguKnH6yEy0q5iubpgPGujYQPjEdrHDvrHcQrHTdPjDYn1b3PW9LPj0QnH9h0vQfIjd_pgPYUARhIaubpgPYUHYQPjN1P1DdrjDOrjnvFMF-uWdCIZwkrBtfmhC8PH98mvqVsvPCmyqOmyOMsvPCIgGdsLK8nith0vqd0hP-5HDhIh-1Iy-b5HThIh-1Iy-k5Hcknz3QnjT8rjc8nHnhuyOYIZTqnaukmgF6UHYhpgP-XZw-UhEquh7_0vNh0AQh5iYQFh-VuybqFMRGujY1PHbdP1D3Pj9zPjNOnBuWUAVGujYdnjDdrAcOPzYknvEksHwhP1NVmhDdmzY1uyDLnjT3m1whn19h0A-b5HbhuyOYpyEqnWELPWbdn1TzP1EvnHnhuyOYIZTqnauk0h-WuHYznWnh0hRbpgcqpZwY0jCfsvFJsWN3shPfUiqlIyu6Uh0fnWELPWbdn1TzP1EvnHP3sMPCIAd_FhwG0LKf01YvFMPdmh-b5HckP1ndP1TLFhPzuy7YuyEqnHmOP1Nzn1NvnHEdPjDvFMK60h7VpyEqFhwG0vP65H6tnHTh0vR_uhP65H9huA-1UAtqnHDYnBu1uyQhUAtqnHDYnBuQ5Hc8nWc3nHbzP19vPH9YnjmQPaukmyI-UMRV5HDhmh-b5HczPauLpyQbmv7zujYkFh7k0AR8uAV-XgIf0hEqnauzuyP6UAQhUAqL5HThmyFY5yu6UhIWpA78g1cknHNQnWT3gvF60vRtnE&version=&psid=144591692190541439868747118&entinfo=24769530274613_0")
        if r:
            for k, v in r.items():
                f.write(k+"|"+v+"\n")

if __name__ == "__main__":
    #run()
    #r = wuba_get_house_list("chaoyang")
    test()
