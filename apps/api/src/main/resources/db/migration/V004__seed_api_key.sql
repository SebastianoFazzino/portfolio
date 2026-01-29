INSERT INTO portfolio.api_keys (
    id,
    key,
    client,
    scopes,
    created_by
) VALUES (
             gen_random_uuid(),
             'b9fe200a2637321177488ff6ad9fbef0e1c9bc956321c14ea62dab3acf92392e',
             'bootstrap',
             ARRAY['admin:*'],
             'manual'
         );
