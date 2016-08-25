# sentiment-analysis
Modern techniques for sentiment analysis
See nlp-compromise for more examples.

We use Sentiment analysis as a classification technique to extract and process any emotional information from the text(traditionally) whilesome researchers are specifically focused on performing the same techniques on images.

You can read more about it here.

The simplest form of sentiment analysis is to use a dictionary of positive and negative words. Each word in a sentence has a score, typically +1 for positive sentiment and -1 for negative. Then, we simply add up the scores of all the words in the sentence to get a final sentiment total. Clearly, this has many limitations, one of them being it totally ignores the context surrounding the word. For example, in our simple model the phrase “not good” may be classified as 0 sentiment, given “not” has a score of -1 and “good” a score of +1. A human would likely classify “not good” as negative, despite the presence of “good”.

The 2nd most simple technique is to use something called "Bag of words". Its a simple dictionary lookup for the words appearing in the sentence(regardless of order) and to place these words in a 1 by N matrix. These words must have a value for sentiment predetermined either from an existing dictionary of words or create a subset dictionary with only the words appearing in the text. So this ultimately deals with problems of naivly assigning +1 and -1 value by understanding the intensity of the words...but what the heck its still pretty naive because it really doesnt get context:

eg:

```"That's true, I am not a fan."```

        vs
```"That's not true, I am a fan."```
