When(/^the application starts$/) do
  
end

Given(/^I am logged into the application$/) do
  performAction('enter_text_into_id_field',"android_1", "textUsername")
  performAction('enter_text_into_id_field',"Password1", "textPassword")
  performAction('press',"Register")
  performAction('wait_for_text', 'Dispense', 20)
end