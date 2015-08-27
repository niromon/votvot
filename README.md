# votvot


#Front End
To install the front end libraries :
```
cd VoteVoteServer/src/main/webapp
bower install
```


#Bower troubles
If permission problems appear it is probably because the package folder is not granted for the good user, so run this :
```
sudo chown username:staff /Users/username/.cache/bower/packages/
```

If weird behaviors appear it is probably a cache problem, so run this :
```
bower cache clean
```
