This project is born because I wanted to start running again.
I knew a really good training plan for beginner
([in french] : http://www.jogging-running.com/article-323737.html), but didn't manage to find
any interval chronometer that doesn't require to define a repeated sequence of the exact same
intervals and that doesn't ask for extravagant permissions.

As I was nearly a beginner on Android, I copied the chronometer project from
pyledevehat (https://github.com/pyledevehat/chronos), I migrated it to Android Studio NG,
and I started to enrich it in order to add the missing functionalities I needed :
- predefined intervals
- different colors according to the interval type
- a reverted display of the time (because when the phone is on my arm, I see it upside-down)
- the ability to count up or down
- some sounds to inform at interval switch
I also removed some functions I found useless for my usage (intermediate times, for instance).

I plan to add :
- a training plan editor (yet, my starter plan is hard-coded). This will require to use a database.
- an history of the training sessions
- a GPS tracker, in order to keep some tracks in the training history, with, maybe a graphical
 representation in-app.

As the pyledevehat chronometer I used as a model, this project is released under GPL v3.

It contains the following external resources :
* Sounds :
    - "ding.wav" and "dingding.wav" : extracted from https://www.freesound.org/people/JohnsonBrandEditing/sounds/173932/ (sound by JohnsonBrandEditing, License : Creative Commons 0)
    - "bell.wav" : from https://www.freesound.org/people/tec%20studios/sounds/99625/  (sound by tec studios, License : Creative Commons 0)
    - "churchBell.wav" : from https://www.freesound.org/people/dsp9000/sounds/76405/ (sound by dsp9000, License : Creative Commons 0)
    - "alarm.wav" : from https://www.freesound.org/people/JarAxe/sounds/204424/ (sound by JarAxe, Licence : CC BY 3.0)
    - "deepHorn.mp3" : from https://www.freesound.org/people/Vendarro/sounds/328541/ (sound by Vendarro, License : Creative Commons 0)
    - "bikeHorn.wav" : from https://www.freesound.org/people/Stickinthemud/sounds/27881/ (sound by Stickinthemud, Licence : CC BY 3.0)
* .gitignore : from https://github.com/github/gitignore (Licence : CC0 1.0)
* icon 'chronometer.png' from https://pixabay.com/en/icon-stopwatch-clock-time-black-157350/ (CC0)


