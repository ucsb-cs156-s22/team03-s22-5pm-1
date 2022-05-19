const reviewsFixtures = {
    oneReviews: {
        "id" : 1,
        "itemId" : 2,
        "reviewerEmail" : "cgaucho@ucsb.edu",
        "dateReviewed" : "2022-05-01T00:00:00",
        "stars" : 4,
        "comments" : "very delicious"
    },
    threeReviews: [
        {
            "id" : 1,
            "itemId" : 12,
            "reviewerEmail" : "chriss@ucsb.edu",
            "dateReviewed" : "2021-12-22T11:50:59",
            "stars" : 3,
            "comments" : "It was alright :/"
        },
        {
            "id" : 2,
            "itemId" : 20,
            "reviewerEmail" : "gogauchos@ucsb.edu",
            "dateReviewed" : "2022-01-31T05:15:00",
            "stars" : 1,
            "comments" : "gave me food poisoning"
        },
        {
            "id" : 3,
            "itemId" : 5,
            "reviewerEmail" : "storketower@ucsb.edu",
            "dateReviewed" : "2022-05-03T00:13:00",
            "stars" : 5,
            "comments" : "soooo good. 10/10"
        }
    ]
};

export { reviewsFixtures };