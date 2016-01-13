# Excalibot
aPing sample plugin

Sample plugin, returns the average ping from a number of bots to the defined address.

## Usage
	aping address:<address> bots:<number of bots>

## Dependencies

Needs common.jar from common
Also the manifest file needs to declare the path to the starting classes
as well as the command the plugin responds to.
		
	Manifest-Version: 1.0
	Command: aping
	ArthurPlug: org.powertrip.excalibot.common.plugins.aping.Server
	KnightPlug: org.powertrip.excalibot.common.plugins.aping.Bot


## Questions?

	