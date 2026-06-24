# Conversation Capture: Workstream Surface Intent Routing

## Trigger

The user reviewed `surface-issue.png`, showing an authorized user in the User Admin workstream asking to `create organization "Org 1"`. The displayed response said the User Admin agent could not create the organization from the agent session and recommended opening a dedicated organization/admin surface.

## Findings discussed

- The user was authenticated and may be authorized for SaaS Owner Organization creation.
- The refusal happened because the request went through the free-form model-backed User Admin agent message path, not the structured Organization Admin surface/action path.
- Existing surface actions already include backend-authorized Organization Admin actions such as opening the create form and submitting organization creation.
- Existing workstream actions behave like capability tools from the surface perspective, but workstream agents currently do not invoke those actions.

## Direction chosen

The user preferred a safer deterministic routing approach instead of giving agents direct mutation tools:

- route natural-language composer requests to appropriate surfaces where possible;
- prepopulate surfaces with safe inferred fields;
- let the user review and submit through existing surface actions;
- keep command submission by agents out of scope for now;
- make routing faster than waiting for model responses and use it to train users on structured surfaces;
- make each workstream agent familiar with the surfaces in its workstream.

## Explicit non-decision

The idea of later allowing agents to submit selected commands after explicit confirmation was discussed but intentionally excluded from this mini-project. That should be planned separately only if the surface-routing approach is insufficient.

## Initial example acceptance path

Input:

```text
create organization "Org 1"
```

Expected initial behavior:

1. backend deterministic router recognizes a User Admin Organization Create intent;
2. no model call is needed;
3. a workstream request item is appended indicating that the Organization Create form was opened from the user request;
4. the Organization Create surface is returned with safe prefill data such as `organizationName = Org 1` and an appropriate reason hint;
5. no organization is created until the user submits the surface's backend-authorized action.
