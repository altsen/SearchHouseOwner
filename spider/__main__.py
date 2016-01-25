import re
import requests
import bs4
import codecs

def get_house_list(district):
    result = []
    for i in range(3):
        r = requests.get("http://bj.ganji.com/fang1/"+district+"/o%d"%i)
        result += re.findall("/fang./[^.]*\.htm", r.text)
    return set(map(lambda x: "http://bj.ganji.com"+x, result))

def get_house_page(url):
    result = dict()
    r = requests.get(url)
    soup = bs4.BeautifulSoup(r.text, "html.parser")
    result['title'] = soup.find("h1", "title-name").text
    basic = soup.find("div", class_="basic-box")
    result['img'] = basic.find("div", "basic-imgs-big").find("img")['src']
    #result['price'] = basic.find("b", "basic-info-price").text
    ul = basic.find("ul", "basic-info-ul")
    lis = ul.find_all("li")
    result["price"] = lis[0].find("b", "basic-info-price").text
    result["huxing"] = lis[1].text.strip()
    result["gaikuang"] = lis[2].text.strip()
    result["louceng"] = lis[3].text.strip()
    result["xiaoqu"] = lis[4].find('a')["title"]
    result["weizhi"] = "-".join(map(lambda x: x.text, lis[5].find_all('a')))
    result["peizhi"] = lis[7].text
    
    return result

if __name__ == "__main__":
    #pages = get_house_pages("chaoyang")
    r = get_house_page('http://bj.ganji.com/fang1/1812832959x.htm')
    with codecs.open("output.txt", "w", "utf-8") as f:
        for k, v in r.items():
            f.write(k+"|"+v+"\n")
