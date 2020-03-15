package main

import (
  "fmt"
  "io/ioutil"
  "net/http"
  "os"
  "strings"
  "golang.org/x/net/proxy"
)

const URL = "http://www.example.com"

func main() {
  proxy_data := strings.Split(os.Getenv("IPB_SOCKS5"), "@")
  proxy_addr := proxy_data[1]
  auth_data := strings.Split(proxy_data[0], ":")
  auth := proxy.Auth{
    User: auth_data[0],
    Password: auth_data[1],
  }

  dialer, err := proxy.SOCKS5("tcp", proxy_addr, &auth, proxy.Direct)
  if err != nil {
    fmt.Fprintln(os.Stderr, "can't connect to the proxy:", err)
    os.Exit(1)
  }
  httpTransport := &http.Transport{}
  httpClient := &http.Client{Transport: httpTransport}

  httpTransport.Dial = dialer.Dial

  req, err := http.NewRequest("GET", URL, nil)
  if err != nil {
    fmt.Fprintln(os.Stderr, "can't create request:", err)
    os.Exit(2)
  }
  resp, err := httpClient.Do(req)
  if err != nil {
    fmt.Fprintln(os.Stderr, "can't GET page:", err)
    os.Exit(3)
  }
  defer resp.Body.Close()
  b, err := ioutil.ReadAll(resp.Body)
  if err != nil {
    fmt.Fprintln(os.Stderr, "error reading body:", err)
    os.Exit(4)
  }
  fmt.Println(string(b))
}